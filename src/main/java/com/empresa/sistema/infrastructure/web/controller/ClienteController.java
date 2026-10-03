package com.empresa.sistema.infrastructure.web.controller;

import com.empresa.sistema.application.port.in.AnexoClienteUseCase;
import com.empresa.sistema.application.port.in.ClienteUseCase;
import com.empresa.sistema.application.port.in.CnaeReferenciaUseCase;
import com.empresa.sistema.application.port.in.FornecedorUseCase;
import com.empresa.sistema.application.port.in.NotaFiscalEntradaUseCase;
import com.empresa.sistema.application.port.in.NotaFiscalSaidaUseCase;
import com.empresa.sistema.application.port.in.HistoricoFaturamentoUseCase;
import com.empresa.sistema.application.port.in.ReducaoAliquotaUseCase;
import com.empresa.sistema.application.port.in.CredencialUseCase;
import com.empresa.sistema.application.port.in.UsuarioUseCase;
import com.empresa.sistema.domain.model.Cliente;
import com.empresa.sistema.domain.model.ClienteCnae;
import com.empresa.sistema.domain.model.ClienteCodigoServico;
import com.empresa.sistema.domain.model.Credencial;
import com.empresa.sistema.domain.model.FiltroCliente;
import com.empresa.sistema.domain.model.PaginaCliente;
import com.empresa.sistema.domain.model.UsuarioResumo;
import com.empresa.sistema.infrastructure.security.UsuarioAutenticado;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller: parte da infraestrutura, so fala com o mundo externo (HTTP)
 * e com as portas de entrada ClienteUseCase/UsuarioUseCase/CredencialUseCase.
 * Nao conhece JPA, nao conhece ClienteJpaEntity -- so os objetos de dominio.
 */
@Controller
@RequestMapping("/clientes")
public class ClienteController {

    /** Quantos clientes mostrar por pagina na tela de lista. */
    private static final int TAMANHO_PAGINA = 20;

    private final ClienteUseCase clienteUseCase;
    private final UsuarioUseCase usuarioUseCase;
    private final CredencialUseCase credencialUseCase;
    private final AnexoClienteUseCase anexoClienteUseCase;
    private final CnaeReferenciaUseCase cnaeReferenciaUseCase;
    private final FornecedorUseCase fornecedorUseCase;
    private final NotaFiscalEntradaUseCase notaFiscalEntradaUseCase;
    private final NotaFiscalSaidaUseCase notaFiscalSaidaUseCase;
    private final HistoricoFaturamentoUseCase historicoFaturamentoUseCase;
    private final ReducaoAliquotaUseCase reducaoAliquotaUseCase;
    private final ObjectMapper objectMapper;

    public ClienteController(ClienteUseCase clienteUseCase, UsuarioUseCase usuarioUseCase,
                              CredencialUseCase credencialUseCase, AnexoClienteUseCase anexoClienteUseCase,
                              CnaeReferenciaUseCase cnaeReferenciaUseCase, FornecedorUseCase fornecedorUseCase,
                              NotaFiscalEntradaUseCase notaFiscalEntradaUseCase,
                              NotaFiscalSaidaUseCase notaFiscalSaidaUseCase,
                              HistoricoFaturamentoUseCase historicoFaturamentoUseCase,
                              ReducaoAliquotaUseCase reducaoAliquotaUseCase,
                              ObjectMapper objectMapper) {
        this.clienteUseCase = clienteUseCase;
        this.usuarioUseCase = usuarioUseCase;
        this.objectMapper = objectMapper;
        this.credencialUseCase = credencialUseCase;
        this.anexoClienteUseCase = anexoClienteUseCase;
        this.cnaeReferenciaUseCase = cnaeReferenciaUseCase;
        this.fornecedorUseCase = fornecedorUseCase;
        this.notaFiscalEntradaUseCase = notaFiscalEntradaUseCase;
        this.notaFiscalSaidaUseCase = notaFiscalSaidaUseCase;
        this.historicoFaturamentoUseCase = historicoFaturamentoUseCase;
        this.reducaoAliquotaUseCase = reducaoAliquotaUseCase;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String termo,
                          @RequestParam(required = false) String estado,
                          @RequestParam(required = false) String regimeTributario,
                          @RequestParam(required = false) Boolean ativo,
                          @RequestParam(required = false) String codigoSistemaExterno,
                          @RequestParam(required = false) Boolean meusClientes,
                          @RequestParam(defaultValue = "0") int pagina,
                          @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
                          Model model) {
        Long responsavelId = (meusClientes != null && meusClientes) ? usuarioAutenticado.getUsuario().getId() : null;
        FiltroCliente filtro = new FiltroCliente(termo, estado, regimeTributario, ativo, codigoSistemaExterno, responsavelId);
        PaginaCliente paginaCliente = clienteUseCase.listarPaginado(filtro, pagina, TAMANHO_PAGINA);
        model.addAttribute("clientes", paginaCliente.getConteudo());
        model.addAttribute("paginaAtual", paginaCliente.getPaginaAtual());
        model.addAttribute("totalPaginas", paginaCliente.getTotalPaginas());
        model.addAttribute("totalElementos", paginaCliente.getTotalElementos());
        model.addAttribute("temAnterior", paginaCliente.isTemAnterior());
        model.addAttribute("temProxima", paginaCliente.isTemProxima());
        model.addAttribute("termo", termo);
        model.addAttribute("estado", estado);
        model.addAttribute("regimeTributario", regimeTributario);
        model.addAttribute("ativo", ativo);
        model.addAttribute("codigoSistemaExterno", codigoSistemaExterno);
        model.addAttribute("meusClientes", meusClientes != null && meusClientes);
        return "clientes/lista";
    }

    /**
     * Visualizacao "tipo planilha": mesma busca/filtro da lista normal, mas
     * numa tabela larga com todas as colunas (incluindo CNAEs e codigos de
     * servico ja abertos, sem precisar clicar em cada cliente) -- pra quem
     * quer só consultar rapido na tela, sem precisar baixar o Excel.
     */
    @GetMapping("/planilha")
    public String planilha(@RequestParam(required = false) String termo,
                            @RequestParam(required = false) String estado,
                            @RequestParam(required = false) String regimeTributario,
                            @RequestParam(required = false) Boolean ativo,
                            @RequestParam(required = false) String codigoSistemaExterno,
                            Model model) {
        FiltroCliente filtro = new FiltroCliente(termo, estado, regimeTributario, ativo, codigoSistemaExterno, null);
        model.addAttribute("clientes", clienteUseCase.listar(filtro));
        model.addAttribute("termo", termo);
        model.addAttribute("estado", estado);
        model.addAttribute("regimeTributario", regimeTributario);
        model.addAttribute("ativo", ativo);
        model.addAttribute("codigoSistemaExterno", codigoSistemaExterno);
        return "clientes/planilha";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("modoEdicao", false);
        model.addAttribute("usuarios", usuarioUseCase.listarAtivos());
        model.addAttribute("reducoes", reducaoAliquotaUseCase.listar());
        return "clientes/form";
    }

    @PostMapping("/novo")
    public String criar(@Valid @ModelAttribute("cliente") Cliente cliente,
                         BindingResult resultado,
                         @RequestParam(required = false) Long responsavelId,
                         @RequestParam(required = false) String cnaesSugeridosJson,
                         @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
                         Model model) {
        if (resultado.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            model.addAttribute("usuarios", usuarioUseCase.listarAtivos());
        model.addAttribute("reducoes", reducaoAliquotaUseCase.listar());
            return "clientes/form";
        }
        cliente.setResponsavel(responsavelId != null ? new UsuarioResumo(responsavelId, null, null) : null);
        Cliente salvo = clienteUseCase.salvar(cliente, usuarioAutenticado.getUsuarioResumo());

        // CNAEs sugeridos pela busca de CNPJ (ver form.html) -- a pessoa ja
        // conferiu na tela antes de salvar. Cada um e adicionado igual seria
        // feito manualmente depois, na tela de detalhe.
        for (ClienteCnae cnae : cnaesSugeridos(cnaesSugeridosJson)) {
            clienteUseCase.adicionarCnae(salvo.getId(), cnae, usuarioAutenticado.getUsuarioResumo());
        }

        return "redirect:/clientes/" + salvo.getId();
    }

    private List<ClienteCnae> cnaesSugeridos(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<ClienteCnae>>() {});
        } catch (JsonProcessingException e) {
            // A busca de CNAE e so uma ajuda de preenchimento -- se o JSON vier
            // corrompido por algum motivo, nao vale travar a criacao do cliente.
            return List.of();
        }
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id,
                           @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                           @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate dataFim,
                           Model model) {
        model.addAttribute("cliente", clienteUseCase.buscarPorId(id));
        model.addAttribute("novaCnae", new ClienteCnae());
        model.addAttribute("novoCodigo", new ClienteCodigoServico());
        model.addAttribute("credenciais", credencialUseCase.listarPorCliente(id));
        model.addAttribute("novaCredencial", new Credencial());
        model.addAttribute("anexos", anexoClienteUseCase.listarPorCliente(id));
        model.addAttribute("cnaesReferencia", cnaeReferenciaUseCase.listar());
        model.addAttribute("fornecedores", fornecedorUseCase.listarPorCliente(id));

        // Credito tributario (Fase 3): sem filtro informado, mostra o resumo dos ultimos 12 meses por padrao.
        LocalDate periodoFim = dataFim != null ? dataFim : LocalDate.now();
        LocalDate periodoInicio = dataInicio != null ? dataInicio : periodoFim.minusMonths(12);
        model.addAttribute("notasFiscais", notaFiscalEntradaUseCase.listarPorCliente(id));
        model.addAttribute("resumoCredito", notaFiscalEntradaUseCase.calcularResumo(id, periodoInicio, periodoFim));
        model.addAttribute("periodoInicio", periodoInicio);
        model.addAttribute("periodoFim", periodoFim);

        // Faturamento historico (Fase 4): NF-e de venda + faturamento manual por ano.
        model.addAttribute("notasFiscaisSaida", notaFiscalSaidaUseCase.listarPorCliente(id));
        model.addAttribute("historicoFaturamento", historicoFaturamentoUseCase.listarPorCliente(id));
        return "clientes/detalhe";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clienteUseCase.buscarPorId(id));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("usuarios", usuarioUseCase.listarAtivos());
        model.addAttribute("reducoes", reducaoAliquotaUseCase.listar());
        return "clientes/form";
    }

    @PostMapping("/{id}/editar")
    public String editar(@PathVariable Long id,
                          @Valid @ModelAttribute("cliente") Cliente cliente,
                          BindingResult resultado,
                          @RequestParam(required = false) Long responsavelId,
                          @RequestParam(required = false) String cnaesSugeridosJson,
                          @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
                          Model model) {
        if (resultado.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("usuarios", usuarioUseCase.listarAtivos());
        model.addAttribute("reducoes", reducaoAliquotaUseCase.listar());
            return "clientes/form";
        }
        cliente.setId(id);
        cliente.setResponsavel(responsavelId != null ? new UsuarioResumo(responsavelId, null, null) : null);
        clienteUseCase.salvar(cliente, usuarioAutenticado.getUsuarioResumo());

        for (ClienteCnae cnae : cnaesSugeridos(cnaesSugeridosJson)) {
            clienteUseCase.adicionarCnae(id, cnae, usuarioAutenticado.getUsuarioResumo());
        }

        return "redirect:/clientes/" + id;
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        clienteUseCase.excluir(id, usuarioAutenticado.getUsuarioResumo());
        return "redirect:/clientes";
    }

    @PostMapping("/{id}/cnaes")
    public String adicionarCnae(@PathVariable Long id,
                                 @ModelAttribute ClienteCnae novaCnae,
                                 @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        clienteUseCase.adicionarCnae(id, novaCnae, usuarioAutenticado.getUsuarioResumo());
        return "redirect:/clientes/" + id;
    }

    @PostMapping("/{id}/cnaes/{cnaeId}/excluir")
    public String removerCnae(@PathVariable Long id, @PathVariable Long cnaeId,
                               @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        clienteUseCase.removerCnae(id, cnaeId, usuarioAutenticado.getUsuarioResumo());
        return "redirect:/clientes/" + id;
    }

    @PostMapping("/{id}/codigos")
    public String adicionarCodigo(@PathVariable Long id,
                                   @ModelAttribute ClienteCodigoServico novoCodigo,
                                   @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        clienteUseCase.adicionarCodigoServico(id, novoCodigo, usuarioAutenticado.getUsuarioResumo());
        return "redirect:/clientes/" + id;
    }

    @PostMapping("/{id}/codigos/{codigoId}/excluir")
    public String removerCodigo(@PathVariable Long id, @PathVariable Long codigoId,
                                 @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        clienteUseCase.removerCodigoServico(id, codigoId, usuarioAutenticado.getUsuarioResumo());
        return "redirect:/clientes/" + id;
    }
}
