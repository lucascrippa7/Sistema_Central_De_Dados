package com.empresa.sistema.infrastructure.web.controller;

import com.empresa.sistema.application.port.in.NotaFiscalEntradaUseCase;
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

/**
 * Importar/remover NF-e: restrito pelo SecurityConfig do mesmo jeito que
 * fornecedores/anexos (ADMINISTRADOR/EDITOR/EDITOR_SENHAS). Ver a lista e o
 * resumo de credito (secao na tela de detalhe do cliente) e liberado para
 * qualquer funcionario, igual o resto.
 */
@Controller
public class NotaFiscalEntradaController {

    private final NotaFiscalEntradaUseCase notaFiscalEntradaUseCase;

    public NotaFiscalEntradaController(NotaFiscalEntradaUseCase notaFiscalEntradaUseCase) {
        this.notaFiscalEntradaUseCase = notaFiscalEntradaUseCase;
    }

    @PostMapping("/clientes/{clienteId}/notas-fiscais/importar")
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

        ResultadoImportacaoNfe resultado = notaFiscalEntradaUseCase.importar(
                clienteId, arquivosImportados, usuarioAutenticado.getUsuarioResumo());

        if (resultado.isTudoOk()) {
            redirectAttributes.addFlashAttribute("mensagemSucesso",
                    resultado.getImportadosComSucesso() + " nota(s) fiscal(is) importada(s) com sucesso.");
        } else {
            redirectAttributes.addFlashAttribute("mensagemSucesso",
                    resultado.getImportadosComSucesso() + " nota(s) importada(s), " + resultado.getErros().size()
                            + " arquivo(s) com problema:");
            redirectAttributes.addFlashAttribute("errosImportacaoNfe", resultado.getErros());
        }
        return "redirect:/clientes/" + clienteId;
    }

    @PostMapping("/notas-fiscais/{id}/excluir")
    public String remover(@PathVariable Long id,
                           @RequestParam Long clienteId,
                           @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        notaFiscalEntradaUseCase.remover(id, usuarioAutenticado.getUsuarioResumo());
        return "redirect:/clientes/" + clienteId;
    }
}
