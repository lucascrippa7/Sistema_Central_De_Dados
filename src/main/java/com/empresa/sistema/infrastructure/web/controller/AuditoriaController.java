package com.empresa.sistema.infrastructure.web.controller;

import com.empresa.sistema.application.port.in.AuditoriaUseCase;
import com.empresa.sistema.application.port.in.UsuarioUseCase;
import com.empresa.sistema.domain.model.AcaoAuditoria;
import com.empresa.sistema.domain.model.FiltroAuditoria;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

/**
 * Tela de consulta do log de auditoria -- so administrador acessa (ver
 * SecurityConfig). Aqui e so leitura: quem grava no log e o
 * AuditoriaUseCase.registrar(...), chamado pelos services de cada modulo.
 */
@Controller
@RequestMapping("/auditoria")
public class AuditoriaController {

    /** Entidades que hoje geram registro de auditoria no sistema. */
    private static final String[] ENTIDADES = {"CLIENTE", "CREDENCIAL", "USUARIO", "EVENTO"};

    private final AuditoriaUseCase auditoriaUseCase;
    private final UsuarioUseCase usuarioUseCase;

    public AuditoriaController(AuditoriaUseCase auditoriaUseCase, UsuarioUseCase usuarioUseCase) {
        this.auditoriaUseCase = auditoriaUseCase;
        this.usuarioUseCase = usuarioUseCase;
    }

    @GetMapping
    public String lista(@RequestParam(required = false) String entidade,
                         @RequestParam(required = false) AcaoAuditoria acao,
                         @RequestParam(required = false) Long usuarioId,
                         @RequestParam(required = false) LocalDate dataInicio,
                         @RequestParam(required = false) LocalDate dataFim,
                         Model model) {
        FiltroAuditoria filtro = new FiltroAuditoria(entidade, acao, usuarioId, dataInicio, dataFim);
        model.addAttribute("logs", auditoriaUseCase.listar(filtro));
        model.addAttribute("entidade", entidade);
        model.addAttribute("acao", acao);
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("entidades", ENTIDADES);
        model.addAttribute("acoes", AcaoAuditoria.values());
        model.addAttribute("usuarios", usuarioUseCase.listarTodos());
        return "auditoria/lista";
    }
}
