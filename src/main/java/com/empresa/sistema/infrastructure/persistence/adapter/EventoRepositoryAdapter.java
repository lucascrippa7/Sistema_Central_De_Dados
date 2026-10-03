package com.empresa.sistema.infrastructure.persistence.adapter;

import com.empresa.sistema.application.port.out.EventoRepositoryPort;
import com.empresa.sistema.domain.exception.EventoNaoEncontradoException;
import com.empresa.sistema.domain.model.Evento;
import com.empresa.sistema.infrastructure.persistence.entity.EventoJpaEntity;
import com.empresa.sistema.infrastructure.persistence.mapper.EventoMapper;
import com.empresa.sistema.infrastructure.persistence.repository.EventoJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EventoRepositoryAdapter implements EventoRepositoryPort {

    private final EventoJpaRepository eventoJpaRepository;
    private final EventoMapper eventoMapper;

    public EventoRepositoryAdapter(EventoJpaRepository eventoJpaRepository, EventoMapper eventoMapper) {
        this.eventoJpaRepository = eventoJpaRepository;
        this.eventoMapper = eventoMapper;
    }

    @Override
    public List<Evento> findByPeriodo(LocalDate inicio, LocalDate fim, Long usuarioId) {
        List<EventoJpaEntity> entidades = usuarioId == null
                ? eventoJpaRepository.findByDataBetweenOrderByDataAscHorarioAsc(inicio, fim)
                : eventoJpaRepository.findByDataBetweenAndUsuarioIdOrderByDataAscHorarioAsc(inicio, fim, usuarioId);

        return entidades.stream().map(eventoMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Evento> findById(Long id) {
        return eventoJpaRepository.findById(id).map(eventoMapper::toDomain);
    }

    @Override
    public Evento save(Evento evento) {
        EventoJpaEntity entity = evento.getId() == null
                ? new EventoJpaEntity()
                : eventoJpaRepository.findById(evento.getId())
                    .orElseThrow(() -> new EventoNaoEncontradoException(evento.getId()));

        eventoMapper.aplicarNaEntidade(evento, entity);
        EventoJpaEntity salva = eventoJpaRepository.save(entity);
        return eventoMapper.toDomain(salva);
    }

    @Override
    public void deleteById(Long id) {
        eventoJpaRepository.deleteById(id);
    }

    @Override
    public List<Evento> findMestresDeSeriesAtivas() {
        return eventoJpaRepository.findMestresDeSeriesAtivas().stream()
                .map(eventoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existeOcorrenciaParaMes(Long serieId, int ano, int mes) {
        return eventoJpaRepository.existsBySerieIdAndSerieAnoReferenciaAndSerieMesReferencia(serieId, ano, mes);
    }
}
