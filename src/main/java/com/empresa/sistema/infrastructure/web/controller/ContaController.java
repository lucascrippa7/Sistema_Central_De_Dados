package com.empresa.sistema.infrastructure.web.controller;

import com.empresa.sistema.application.port.in.UsuarioUseCase;
import com.empresa.sistema.domain.exception.SenhaAtualIncorretaException;
import com.empresa.sistema.infrastructure.security.UsuarioAutenticado;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Rotas /conta/** -- disponiveis para QUALQUER usuario autenticado,
 * independente do perfil (nao ha restricao especifica no SecurityConfig,
 * cai na regra geral "anyRequest().authenticated()").
 */
@Controller
@RequestMapping("/conta")
public class ContaController {

    private final UsuarioUseCase usuarioUseCase;

    public ContaController(UsuarioUseCase usuarioUseCase) {
        this.usuarioUseCase = usuarioUseCase;
    }

    @GetMapping("/senha")
    public String form(@AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado, Model model) {
        model.addAttribute("emailAtual", usuarioUseCase.buscarPorId(usuarioAutenticado.getUsuario().getId()).getEmail());
        return "conta/trocar-senha";
    }

    @PostMapping("/email")
    public String atualizarEmail(@RequestParam String email,
                                  @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
                                  Model model) {
        usuarioUseCase.atualizarProprioEmail(usuarioAutenticado.getUsuario().getId(), email);
        model.addAttribute("emailAtual", email);
        model.addAttribute("sucessoEmail", true);
        return "conta/trocar-senha";
    }

    @PostMapping("/senha")
    public String trocar(@RequestParam String senhaAtual,
                          @RequestParam String novaSenha,
                          @RequestParam String confirmarSenha,
                          @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
                          Model model) {

        model.addAttribute("emailAtual", usuarioUseCase.buscarPorId(usuarioAutenticado.getUsuario().getId()).getEmail());

        if (novaSenha == null || novaSenha.length() < 6) {
            model.addAttribute("erro", "A nova senha precisa ter pelo menos 6 caracteres");
            return "conta/trocar-senha";
        }
        if (!novaSenha.equals(confirmarSenha)) {
            model.addAttribute("erro", "A confirmacao nao bate com a nova senha");
            return "conta/trocar-senha";
        }

        try {
            usuarioUseCase.trocarPropriaSenha(usuarioAutenticado.getUsuario().getId(), senhaAtual, novaSenha);
        } catch (SenhaAtualIncorretaException e) {
            model.addAttribute("erro", e.getMessage());
            return "conta/trocar-senha";
        }

        model.addAttribute("sucesso", true);
        return "conta/trocar-senha";
    }
}
