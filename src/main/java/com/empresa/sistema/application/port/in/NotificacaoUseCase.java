package com.empresa.sistema.application.port.in;

import com.empresa.sistema.domain.model.Evento;
import com.empresa.sistema.domain.model.UsuarioResumo;

import java.util.List;

public interface NotificacaoUseCase {

    /**
     * Eventos do usuario logado que devem notificar hoje: os que caem
     * exatamente hoje, ou os que tem um lembrete configurado (X dias de
     * antecedencia) que caia em hoje.
     */
    List<Evento> listarNotificacoesDeHoje(UsuarioResumo usuarioLogado);

    /**
     * Roda a rotina diaria de e-mail: para cada funcionario com e-mail
     * cadastrado, envia um e-mail com os eventos que notificam hoje (e
     * ainda nao foram notificados hoje). Chamado pelo NotificacaoScheduler.
     */
    void enviarNotificacoesPorEmailDoDia();
}
