package com.empresa.sistema.infrastructure.web.controller;

import com.empresa.sistema.application.port.in.AuditoriaUseCase;
import com.empresa.sistema.application.port.in.ClienteUseCase;
import com.empresa.sistema.application.port.in.EventoUseCase;
import com.empresa.sistema.domain.model.Cliente;
import com.empresa.sistema.domain.model.Evento;
import com.empresa.sistema.domain.model.FiltroAuditoria;
import com.empresa.sistema.domain.model.FiltroCliente;
import com.empresa.sistema.domain.model.LogAuditoria;
import com.empresa.sistema.domain.model.TipoAcesso;
import com.empresa.sistema.infrastructure.security.UsuarioAutenticado;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PaginaController {

    /** Quantas atualizacoes recentes do proprio usuario mostrar no dashboard. */
    private static final int LIMITE_MINHAS_ATUALIZACOES = 5;

    /** Quantos dias a frente olhar para os "proximos eventos" do dashboard. */
    private static final int DIAS_PROXIMOS_EVENTOS = 7;

    private final ClienteUseCase clienteUseCase;
    private final EventoUseCase eventoUseCase;
    private final AuditoriaUseCase auditoriaUseCase;

    public PaginaController(ClienteUseCase clienteUseCase, EventoUseCase eventoUseCase,
                             AuditoriaUseCase auditoriaUseCase) {
        this.clienteUseCase = clienteUseCase;
        this.eventoUseCase = eventoUseCase;
        this.auditoriaUseCase = auditoriaUseCase;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado, Model model) {
        boolean administrador = usuarioAutenticado.getTipoAcesso() == TipoAcesso.ADMINISTRADOR;

        List<Cliente> todosClientes = clienteUseCase.listar(new FiltroCliente(null, null, null, null, null, null));
        long totalAtivos = todosClientes.stream().filter(Cliente::isAtivo).count();

        // "Minhas atualizacoes": as ultimas coisas que o PROPRIO usuario logado
        // fez no sistema (criar/editar/excluir cliente, evento, etc) -- reaproveita
        // o log de auditoria que ja existe, so filtrando por usuario e limitando.
        FiltroAuditoria filtroMinhasAcoes = new FiltroAuditoria(null, null, usuarioAutenticado.getUsuario().getId(), null, null);
        List<LogAuditoria> minhasAtualizacoes = auditoriaUseCase.listar(filtroMinhasAcoes).stream()
                .limit(LIMITE_MINHAS_ATUALIZACOES)
                .collect(Collectors.toList());

        List<Evento> proximosEventos = eventoUseCase.listarProximosDias(
                DIAS_PROXIMOS_EVENTOS, usuarioAutenticado.getUsuarioResumo(), administrador);

        model.addAttribute("totalClientes", todosClientes.size());
        model.addAttribute("totalClientesAtivos", totalAtivos);
        model.addAttribute("minhasAtualizacoes", minhasAtualizacoes);
        model.addAttribute("proximosEventos", proximosEventos);
        model.addAttribute("administrador", administrador);
        return "dashboard";
    }

    @GetMapping("/")
    public String raiz() {
        return "redirect:/dashboard";
    }
}
