package com.empresa.sistema.infrastructure.persistence.repository;

import com.empresa.sistema.infrastructure.persistence.entity.HistoricoFaturamentoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HistoricoFaturamentoJpaRepository extends JpaRepository<HistoricoFaturamentoJpaEntity, Long> {
    Optional<HistoricoFaturamentoJpaEntity> findByClienteIdAndAno(Long clienteId, int ano);

    List<HistoricoFaturamentoJpaEntity> findByClienteIdOrderByAnoDesc(Long clienteId);

    void deleteByClienteIdAndAno(Long clienteId, int ano);
}
