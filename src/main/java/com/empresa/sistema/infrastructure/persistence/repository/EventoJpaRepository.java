package com.empresa.sistema.infrastructure.persistence.repository;

import com.empresa.sistema.infrastructure.persistence.entity.EventoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface EventoJpaRepository extends JpaRepository<EventoJpaEntity, Long> {

    /** Usado para o administrador -- ve os eventos de todo mundo no periodo. */
    List<EventoJpaEntity> findByDataBetweenOrderByDataAscHorarioAsc(LocalDate inicio, LocalDate fim);

    /** Usado para um funcionario comum -- ve so os proprios eventos no periodo. */
    List<EventoJpaEntity> findByDataBetweenAndUsuarioIdOrderByDataAscHorarioAsc(LocalDate inicio, LocalDate fim, Long usuarioId);

    /**
     * Os eventos "mestre" de series recorrentes ainda ativas -- e a partir
     * deles que o gerador decide se/como criar as proximas ocorrencias.
     * Mestre = o evento cujo id e igual ao proprio serieId.
     */
    @Query("select e from EventoJpaEntity e where e.recorrente = true and e.serieAtiva = true and e.id = e.serieId")
    List<EventoJpaEntity> findMestresDeSeriesAtivas();

    /** Ja existe uma ocorrencia gerada desta serie para este ano/mes de referencia? */
    boolean existsBySerieIdAndSerieAnoReferenciaAndSerieMesReferencia(Long serieId, Integer ano, Integer mes);
}

