package com.empresa.sistema.infrastructure.web.controller;

import com.empresa.sistema.application.port.in.ClienteExcelUseCase;
import com.empresa.sistema.domain.model.ColunaClientePlanilha;
import com.empresa.sistema.domain.model.FiltroCliente;
import com.empresa.sistema.domain.model.ResultadoImportacao;
import com.empresa.sistema.infrastructure.security.UsuarioAutenticado;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

/**
 * Rotas /clientes/exportar/** e /clientes/modelo-importacao: qualquer
 * usuario autenticado (cai na regra geral do SecurityConfig -- exportar e
 * so ler dado que a pessoa ja pode ver na tela).
 * Rota /clientes/importar (POST): restrita a editores, ja configurado no
 * SecurityConfig desde o inicio do projeto.
 */
@Controller
@RequestMapping("/clientes")
public class ClienteExcelController {

    private static final MediaType XLSX = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final ClienteExcelUseCase clienteExcelUseCase;

    public ClienteExcelController(ClienteExcelUseCase clienteExcelUseCase) {
        this.clienteExcelUseCase = clienteExcelUseCase;
    }

    /** Tela onde a pessoa escolhe quais colunas quer na planilha, antes de baixar. */
    @GetMapping("/exportar/opcoes")
    public String opcoesExportar(@RequestParam(required = false) String termo,
                                  @RequestParam(required = false) String estado,
                                  @RequestParam(required = false) String regimeTributario,
                                  @RequestParam(required = false) Boolean ativo,
                                  @RequestParam(required = false) String codigoSistemaExterno,
                                  Model model) {
        model.addAttribute("termo", termo);
        model.addAttribute("estado", estado);
        model.addAttribute("regimeTributario", regimeTributario);
        model.addAttribute("ativo", ativo);
        model.addAttribute("codigoSistemaExterno", codigoSistemaExterno);
        model.addAttribute("colunasDisponiveis", ColunaClientePlanilha.values());
        return "clientes/exportar-opcoes";
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar(@RequestParam(required = false) String termo,
                                            @RequestParam(required = false) String estado,
                                            @RequestParam(required = false) String regimeTributario,
                                            @RequestParam(required = false) Boolean ativo,
                                            @RequestParam(required = false) String codigoSistemaExterno,
                                            @RequestParam(required = false) Set<ColunaClientePlanilha> colunas) {
        FiltroCliente filtro = new FiltroCliente(termo, estado, regimeTributario, ativo, codigoSistemaExterno, null);
        byte[] planilha = clienteExcelUseCase.exportar(filtro, colunas);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"clientes.xlsx\"")
                .contentType(XLSX)
                .body(planilha);
    }

    @GetMapping("/modelo-importacao")
    public ResponseEntity<byte[]> modelo() {
        byte[] planilha = clienteExcelUseCase.gerarModelo();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"modelo-importacao-clientes.xlsx\"")
                .contentType(XLSX)
                .body(planilha);
    }

    @GetMapping("/importar")
    public String importarForm() {
        return "clientes/importar";
    }

    @PostMapping("/importar")
    public String importar(@RequestParam("arquivo") MultipartFile arquivo,
                            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
                            Model model) throws IOException {
        if (arquivo.isEmpty()) {
            model.addAttribute("erro", "Selecione um arquivo .xlsx para importar.");
            return "clientes/importar";
        }

        ResultadoImportacao resultado = clienteExcelUseCase.importar(arquivo.getInputStream(), usuarioAutenticado.getUsuarioResumo());
        model.addAttribute("resultado", resultado);
        return "clientes/importar";
    }
}
