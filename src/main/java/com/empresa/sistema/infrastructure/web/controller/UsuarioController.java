package com.empresa.sistema.infrastructure.web.controller;

import com.empresa.sistema.application.port.in.UsuarioUseCase;
import com.empresa.sistema.domain.exception.LoginJaExisteException;
import com.empresa.sistema.domain.model.Usuario;
import com.empresa.sistema.infrastructure.security.UsuarioAutenticado;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Rotas /usuarios/** ja sao restritas a ROLE_ADMINISTRADOR pelo
 * SecurityConfig -- nao precisa checar permissao aqui de novo.
 */
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioUseCase usuarioUseCase;

    public UsuarioController(UsuarioUseCase usuarioUseCase) {
        this.usuarioUseCase = usuarioUseCase;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioUseCase.listarTodos());
        return "usuarios/lista";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("modoEdicao", false);
        return "usuarios/form";
    }

    @PostMapping("/novo")
    public String criar(@Valid @ModelAttribute("usuario") Usuario usuario,
                         BindingResult resultado,
                         @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
                         Model model) {
        if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {
            resultado.rejectValue("senha", "obrigatorio", "Informe uma senha inicial");
        }
        if (resultado.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "usuarios/form";
        }
        try {
            usuarioUseCase.criar(usuario, usuarioAutenticado.getUsuarioResumo());
        } catch (LoginJaExisteException e) {
            model.addAttribute("modoEdicao", false);
            model.addAttribute("erro", e.getMessage());
            return "usuarios/form";
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioUseCase.buscarPorId(id));
        model.addAttribute("modoEdicao", true);
        return "usuarios/form";
    }

    @PostMapping("/{id}/editar")
    public String editar(@PathVariable Long id,
                          @Valid @ModelAttribute("usuario") Usuario usuario,
                          BindingResult resultado,
                          @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
                          Model model) {
        if (resultado.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            return "usuarios/form";
        }
        try {
            usuarioUseCase.atualizar(id, usuario, usuarioAutenticado.getUsuarioResumo());
        } catch (LoginJaExisteException e) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("erro", e.getMessage());
            return "usuarios/form";
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/redefinir-senha")
    public String redefinirSenhaForm(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioUseCase.buscarPorId(id));
        return "usuarios/redefinir-senha";
    }

    @PostMapping("/{id}/redefinir-senha")
    public String redefinirSenha(@PathVariable Long id,
                                  @RequestParam String novaSenha,
                                  @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
                                  Model model) {
        if (novaSenha == null || novaSenha.length() < 6) {
            model.addAttribute("usuario", usuarioUseCase.buscarPorId(id));
            model.addAttribute("erro", "A senha precisa ter pelo menos 6 caracteres");
            return "usuarios/redefinir-senha";
        }
        usuarioUseCase.redefinirSenha(id, novaSenha, usuarioAutenticado.getUsuarioResumo());
        return "redirect:/usuarios";
    }
}
