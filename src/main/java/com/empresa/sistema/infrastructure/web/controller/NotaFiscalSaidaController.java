package com.empresa.sistema.infrastructure.web.controller;

import com.empresa.sistema.application.port.in.NotaFiscalEntradaUseCase;
import com.empresa.sistema.application.port.in.NotaFiscalSaidaUseCase;
import com.empresa.sistema.domain.model.ResultadoImportacaoNfe;
import com.empresa.sistema.infrastructure.security.UsuarioAutenticado;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Importar/remover NF-e de venda: mesma restricao de NotaFiscalEntradaController -- alimenta o faturamento historico do cliente. */
@Controller
public class NotaFiscalSaidaController {

    private final NotaFiscalSaidaUseCase notaFiscalSaidaUseCase;

    public NotaFiscalSaidaController(NotaFiscalSaidaUseCase notaFiscalSaidaUseCase) {
        this.notaFiscalSaidaUseCase = notaFiscalSaidaUseCase;
    }

    @PostMapping("/clientes/{clienteId}/notas-fiscais-saida/importar")
    public String importar(@PathVariable Long clienteId,
                            @RequestParam("arquivos") List<MultipartFile> arquivos,
                            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
                            RedirectAttributes redirectAttributes) throws IOException {
        if (arquivos == null || arquivos.isEmpty() || arquivos.stream().allMatch(MultipartFile::isEmpty)) {
            redirectAttributes.addFlashAttribute("erroGeral", "Selecione ao menos um arquivo .xml ou .zip para importar.");
            return "redirect:/clientes/" + clienteId;
        }

        List<NotaFiscalEntradaUseCase.ArquivoImportado> arquivosImportados = new ArrayList<>();
        for (MultipartFile arquivo : arquivos) {
            if (!arquivo.isEmpty()) {
                arquivosImportados.add(new NotaFiscalEntradaUseCase.ArquivoImportado(
                        arquivo.getOriginalFilename(), arquivo.getBytes()));
            }
        }

        ResultadoImportacaoNfe resultado = notaFiscalSaidaUseCase.importar(
                clienteId, arquivosImportados, usuarioAutenticado.getUsuarioResumo());

        if (resultado.isTudoOk()) {
            redirectAttributes.addFlashAttribute("mensagemSucesso",
                    resultado.getImportadosComSucesso() + " nota(s) de venda importada(s) com sucesso.");
        } else {
            redirectAttributes.addFlashAttribute("mensagemSucesso",
                    resultado.getImportadosComSucesso() + " nota(s) importada(s), " + resultado.getErros().size()
                            + " arquivo(s) com problema:");
            redirectAttributes.addFlashAttribute("errosImportacaoNfe", resultado.getErros());
        }
        return "redirect:/clientes/" + clienteId;
    }

    @PostMapping("/notas-fiscais-saida/{id}/excluir")
    public String remover(@PathVariable Long id,
                           @RequestParam Long clienteId,
                           @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        notaFiscalSaidaUseCase.remover(id, usuarioAutenticado.getUsuarioResumo());
        return "redirect:/clientes/" + clienteId;
    }
}
