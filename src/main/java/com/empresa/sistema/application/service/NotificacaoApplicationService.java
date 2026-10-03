package com.empresa.sistema.application.service;

import com.empresa.sistema.application.port.in.NotificacaoUseCase;
import com.empresa.sistema.application.port.in.UsuarioUseCase;
import com.empresa.sistema.application.port.out.EmailPort;
import com.empresa.sistema.application.port.out.EventoRepositoryPort;
import com.empresa.sistema.domain.model.Evento;
import com.empresa.sistema.domain.model.Usuario;
import com.empresa.sistema.domain.model.UsuarioResumo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Calcula quais eventos devem notificar hoje (no proprio dia, ou por um
 * lembrete de X dias de antecedencia) e dispara os e-mails diarios. As
 * notificacoes dentro do sistema (sininho) usam listarNotificacoesDeHoje
 * diretamente, sem depender de e-mail nenhum.
 */
@Service
public class NotificacaoApplicationService implements NotificacaoUseCase {

    /** Ate quantos dias a frente olhar -- cobre qualquer lembrete razoavel configurado. */
    private static final int JANELA_DIAS = 60;

    private final EventoRepositoryPort eventoRepositoryPort;
    private final UsuarioUseCase usuarioUseCase;
    private final EmailPort emailPort;

    public NotificacaoApplicationService(EventoRepositoryPort eventoRepositoryPort,
                                          UsuarioUseCase usuarioUseCase,
                                          EmailPort emailPort) {
        this.eventoRepositoryPort = eventoRepositoryPort;
        this.usuarioUseCase = usuarioUseCase;
        this.emailPort = emailPort;
    }

    @Override
    public List<Evento> listarNotificacoesDeHoje(UsuarioResumo usuarioLogado) {
        LocalDate hoje = LocalDate.now();
        List<Evento> proximos = eventoRepositoryPort.findByPeriodo(hoje, hoje.plusDays(JANELA_DIAS), usuarioLogado.getId());
        return proximos.stream().filter(e -> notificaHoje(e, hoje)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void enviarNotificacoesPorEmailDoDia() {
        LocalDate hoje = LocalDate.now();

        // usuarioId nulo = todo mundo -- precisamos olhar os eventos de
        // todos os funcionarios pra saber quem precisa receber e-mail hoje.
        List<Evento> todos = eventoRepositoryPort.findByPeriodo(hoje, hoje.plusDays(JANELA_DIAS), null);

        Map<Long, List<Evento>> pendentesPorUsuario = todos.stream()
                .filter(e -> notificaHoje(e, hoje))
                .filter(e -> e.getNotificacaoEmailEnviadaEm() == null || !e.getNotificacaoEmailEnviadaEm().equals(hoje))
                .filter(e -> e.getUsuario() != null)
                .collect(Collectors.groupingBy(e -> e.getUsuario().getId()));

        for (Map.Entry<Long, List<Evento>> entrada : pendentesPorUsuario.entrySet()) {
            Usuario usuario = usuarioUseCase.buscarPorId(entrada.getKey());
            if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
                continue;
            }

            emailPort.enviar(usuario.getEmail(), "Lembretes de hoje - Sistema de Clientes",
                    montarCorpoEmail(entrada.getValue(), hoje));

            for (Evento evento : entrada.getValue()) {
                evento.setNotificacaoEmailEnviadaEm(hoje);
                eventoRepositoryPort.save(evento);
            }
        }
    }

    private boolean notificaHoje(Evento evento, LocalDate hoje) {
        if (evento.getData().equals(hoje)) return true;
        long diasAteEvento = ChronoUnit.DAYS.between(hoje, evento.getData());
        return evento.getLembretes() != null && evento.getLembretes().contains((int) diasAteEvento);
    }

    private String montarCorpoEmail(List<Evento> eventos, LocalDate hoje) {
        StringBuilder corpo = new StringBuilder("Voce tem os seguintes lembretes:\n\n");
        for (Evento evento : eventos) {
            long dias = ChronoUnit.DAYS.between(hoje, evento.getData());
            String quando = (dias == 0) ? "HOJE" : ("em " + dias + " dia(s), em " + evento.getData());
            corpo.append("- ").append(evento.getTitulo()).append(" (").append(quando).append(")");
            if (evento.getClienteNome() != null) {
                corpo.append(" - cliente: ").append(evento.getClienteNome());
            }
            corpo.append("\n");
        }
        return corpo.toString();
    }
}
