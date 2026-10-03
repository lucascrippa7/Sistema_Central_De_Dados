package com.empresa.sistema.application.port.out;

import com.empresa.sistema.domain.model.Evento;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventoRepositoryPort {

    /** usuarioId nulo = todos os usuarios (usado so para o administrador). */
    List<Evento> findByPeriodo(LocalDate inicio, LocalDate fim, Long usuarioId);

    Optional<Evento> findById(Long id);

    Evento save(Evento evento);

    void deleteById(Long id);

    /** Mestres (id == serieId) de series recorrentes ainda ativas. */
    List<Evento> findMestresDeSeriesAtivas();

    /** Ja existe uma ocorrencia gerada desta serie para este ano/mes? */
    boolean existeOcorrenciaParaMes(Long serieId, int ano, int mes);
}

