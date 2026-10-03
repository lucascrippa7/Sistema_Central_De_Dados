package com.empresa.sistema.infrastructure.web.controller;

import com.empresa.sistema.application.port.in.NotificacaoUseCase;
import com.empresa.sistema.domain.model.Evento;
import com.empresa.sistema.infrastructure.security.UsuarioAutenticado;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class NotificacaoController {

    private final NotificacaoUseCase notificacaoUseCase;

    public NotificacaoController(NotificacaoUseCase notificacaoUseCase) {
        this.notificacaoUseCase = notificacaoUseCase;
    }

    /** Fragmento HTML carregado via JavaScript no sininho da barra de navegacao. */
    @GetMapping("/notificacoes/fragmento")
    public String fragmento(@AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado, Model model) {
        List<Evento> notificacoes = notificacaoUseCase.listarNotificacoesDeHoje(usuarioAutenticado.getUsuarioResumo());
        model.addAttribute("notificacoes", notificacoes);
        return "fragments/notificacoes :: lista";
    }
}
