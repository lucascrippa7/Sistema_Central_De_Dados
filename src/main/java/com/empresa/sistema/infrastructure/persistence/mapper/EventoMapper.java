package com.empresa.sistema.infrastructure.persistence.mapper;

import com.empresa.sistema.domain.model.Evento;
import com.empresa.sistema.infrastructure.persistence.entity.ClienteJpaEntity;
import com.empresa.sistema.infrastructure.persistence.entity.EventoJpaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class EventoMapper {

    private final UsuarioMapper usuarioMapper;

    public EventoMapper(UsuarioMapper usuarioMapper) {
        this.usuarioMapper = usuarioMapper;
    }

    public Evento toDomain(EventoJpaEntity entity) {
        Evento evento = new Evento();
        evento.setId(entity.getId());
        evento.setTitulo(entity.getTitulo());
        evento.setDescricao(entity.getDescricao());
        evento.setTipo(entity.getTipo());
        evento.setData(entity.getData());
        evento.setHorario(entity.getHorario());

        if (entity.getCliente() != null) {
            evento.setClienteId(entity.getCliente().getId());
            evento.setClienteNome(entity.getCliente().getRazaoSocial());
        }

        evento.setUsuario(usuarioMapper.toResumo(entity.getUsuario()));
        evento.setRecorrente(entity.isRecorrente());
        evento.setSerieId(entity.getSerieId());
        evento.setSerieAtiva(entity.isSerieAtiva());
        evento.setDiaMes(entity.getDiaMes());
        evento.setSerieAnoReferencia(entity.getSerieAnoReferencia());
        evento.setSerieMesReferencia(entity.getSerieMesReferencia());
        evento.setLembretes(new ArrayList<>(entity.getLembretes()));
        evento.setNotificacaoEmailEnviadaEm(entity.getNotificacaoEmailEnviadaEm());
        evento.setCriadoEm(entity.getCriadoEm());
        evento.setCriadoPor(usuarioMapper.toResumo(entity.getCriadoPor()));
        evento.setAtualizadoEm(entity.getAtualizadoEm());
        evento.setAtualizadoPor(usuarioMapper.toResumo(entity.getAtualizadoPor()));
        return evento;
    }

    public void aplicarNaEntidade(Evento evento, EventoJpaEntity entity) {
        entity.setTitulo(evento.getTitulo());
        entity.setDescricao(evento.getDescricao());
        entity.setTipo(evento.getTipo());
        entity.setData(evento.getData());
        entity.setHorario(evento.getHorario());

        if (evento.getClienteId() != null) {
            ClienteJpaEntity clienteRef = new ClienteJpaEntity();
            clienteRef.setId(evento.getClienteId());
            entity.setCliente(clienteRef);
        } else {
            entity.setCliente(null);
        }

        entity.setUsuario(evento.getUsuario() != null
                ? usuarioMapper.referenciaPorId(evento.getUsuario().getId()) : null);
        entity.setRecorrente(evento.isRecorrente());
        entity.setSerieId(evento.getSerieId());
        entity.setSerieAtiva(evento.isSerieAtiva());
        entity.setDiaMes(evento.getDiaMes());
        entity.setSerieAnoReferencia(evento.getSerieAnoReferencia());
        entity.setSerieMesReferencia(evento.getSerieMesReferencia());

        // IMPORTANTE: nunca troque a referencia da lista com setLembretes(new ArrayList<>(...))
        // aqui -- quando a entidade ja existe no banco (edicao), essa lista e' controlada pelo
        // Hibernate, e substituir a referencia da o erro "A collection with
        // cascade=all-delete-orphan was no longer referenced by the owning entity instance".
        // Por isso: sempre mutar a lista que ja existe (clear + addAll), nunca reatribuir.
        entity.getLembretes().clear();
        if (evento.getLembretes() != null) {
            entity.getLembretes().addAll(evento.getLembretes());
        }

        entity.setNotificacaoEmailEnviadaEm(evento.getNotificacaoEmailEnviadaEm());
        entity.setCriadoEm(evento.getCriadoEm());
        entity.setAtualizadoEm(evento.getAtualizadoEm());
        entity.setCriadoPor(evento.getCriadoPor() != null
                ? usuarioMapper.referenciaPorId(evento.getCriadoPor().getId()) : null);
        entity.setAtualizadoPor(evento.getAtualizadoPor() != null
                ? usuarioMapper.referenciaPorId(evento.getAtualizadoPor().getId()) : null);
    }
}
