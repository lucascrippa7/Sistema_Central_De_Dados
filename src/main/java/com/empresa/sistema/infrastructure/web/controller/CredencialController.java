package com.empresa.sistema.infrastructure.web.controller;

import com.empresa.sistema.application.port.in.CredencialUseCase;
import com.empresa.sistema.domain.model.Credencial;
import com.empresa.sistema.infrastructure.security.UsuarioAutenticado;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Adicionar/remover credencial: restrito a ADMINISTRADOR/EDITOR_SENHAS pelo
 * SecurityConfig. Ja revelar a senha e liberado para qualquer funcionario
 * autenticado -- e uso interno da empresa, e todo mundo pode ver a senha
 * (a acao continua registrada na auditoria, ver CredencialApplicationService).
 */
@Controller
public class CredencialController {

    private final CredencialUseCase credencialUseCase;

    public CredencialController(CredencialUseCase credencialUseCase) {
        this.credencialUseCase = credencialUseCase;
    }

    @PostMapping("/clientes/{clienteId}/credenciais")
    public String adicionar(@PathVariable Long clienteId,
                             @ModelAttribute Credencial novaCredencial,
                             @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        credencialUseCase.adicionar(clienteId, novaCredencial, usuarioAutenticado.getUsuarioResumo());
        return "redirect:/clientes/" + clienteId;
    }

    @PostMapping("/clientes/{clienteId}/credenciais/{credencialId}/excluir")
    public String remover(@PathVariable Long clienteId, @PathVariable Long credencialId,
                           @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        credencialUseCase.remover(credencialId, usuarioAutenticado.getUsuarioResumo());
        return "redirect:/clientes/" + clienteId;
    }

    /**
     * "Revelar" nao mostra a senha via GET (isso a deixaria no historico do
     * navegador) -- e um POST que guarda a senha revelada como flash
     * attribute, mostrada so uma vez, na proxima carga da pagina do cliente.
     */
    @PostMapping("/credenciais/{credencialId}/revelar")
    public String revelar(@PathVariable Long credencialId,
                           @RequestParam Long clienteId,
                           @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
                           RedirectAttributes redirectAttributes) {
        String senha = credencialUseCase.revelarSenha(credencialId, usuarioAutenticado.getUsuarioResumo());
        redirectAttributes.addFlashAttribute("credencialRevelada", credencialId);
        redirectAttributes.addFlashAttribute("senhaRevelada", senha);
        return "redirect:/clientes/" + clienteId;
    }
}
