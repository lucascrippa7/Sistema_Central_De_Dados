package com.empresa.sistema.infrastructure.scheduler;

import com.empresa.sistema.application.port.in.EventoUseCase;
import com.empresa.sistema.application.port.in.NotificacaoUseCase;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Rotina diaria do calendario: garante que as series recorrentes ativas
 * tem ocorrencias geradas para os proximos meses, e dispara os e-mails de
 * notificacao do dia (que so saem de verdade se app.notificacoes.email-habilitado=true).
 *
 * Roda todo dia as 6h da manha, e tambem uma vez assim que a aplicacao
 * sobe -- assim, se o servidor ficou desligado e so foi ligado depois das
 * 6h, o dia nao fica sem notificacao.
 */
@Component
public class NotificacaoScheduler {

    /** Quantos meses a frente manter sempre gerados. */
    private static final int MESES_A_FRENTE = 2;

    private final EventoUseCase eventoUseCase;
    private final NotificacaoUseCase notificacaoUseCase;

    public NotificacaoScheduler(EventoUseCase eventoUseCase, NotificacaoUseCase notificacaoUseCase) {
        this.eventoUseCase = eventoUseCase;
        this.notificacaoUseCase = notificacaoUseCase;
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void executarRotinaDiaria() {
        eventoUseCase.garantirOcorrenciasProximosMeses(MESES_A_FRENTE);
        notificacaoUseCase.enviarNotificacoesPorEmailDoDia();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void aoIniciarAplicacao() {
        executarRotinaDiaria();
    }
}
