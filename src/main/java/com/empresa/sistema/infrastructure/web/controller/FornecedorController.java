package com.empresa.sistema.infrastructure.web.controller;

import com.empresa.sistema.application.port.in.FornecedorUseCase;
import com.empresa.sistema.infrastructure.security.UsuarioAutenticado;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Adicionar/remover/reconsultar fornecedor: restrito pelo SecurityConfig
 * do mesmo jeito que CNAE/anexo (ADMINISTRADOR/EDITOR/EDITOR_SENHAS). Ver
 * a lista (secao na tela de detalhe do cliente) e liberado pra qualquer
 * funcionario, igual o resto.
 */
@Controller
public class FornecedorController {

    private final FornecedorUseCase fornecedorUseCase;

    public FornecedorController(FornecedorUseCase fornecedorUseCase) {
        this.fornecedorUseCase = fornecedorUseCase;
    }

    @PostMapping("/clientes/{clienteId}/fornecedores")
    public String adicionar(@PathVariable Long clienteId,
                             @RequestParam String cnpj,
                             @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
                             RedirectAttributes redirectAttributes) {
        String cnpjLimpo = cnpj == null ? "" : cnpj.replaceAll("[^0-9]", "");
        if (cnpjLimpo.length() != 14) {
            redirectAttributes.addFlashAttribute("erroGeral", "Digite um CNPJ valido (14 numeros) antes de adicionar.");
            return "redirect:/clientes/" + clienteId;
        }
        fornecedorUseCase.adicionar(clienteId, cnpjLimpo, usuarioAutenticado.getUsuarioResumo());
        return "redirect:/clientes/" + clienteId;
    }

    @PostMapping("/fornecedores/{id}/reconsultar")
    public String reconsultar(@PathVariable Long id,
                               @RequestParam Long clienteId,
                               @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        fornecedorUseCase.reconsultarSituacao(id, usuarioAutenticado.getUsuarioResumo());
        return "redirect:/clientes/" + clienteId;
    }

    @PostMapping("/fornecedores/{id}/excluir")
    public String remover(@PathVariable Long id,
                           @RequestParam Long clienteId,
                           @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        fornecedorUseCase.remover(id, usuarioAutenticado.getUsuarioResumo());
        return "redirect:/clientes/" + clienteId;
    }
}
