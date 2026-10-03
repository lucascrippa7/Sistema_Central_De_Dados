package com.empresa.sistema.infrastructure.persistence.repository;

import com.empresa.sistema.infrastructure.persistence.entity.NotaFiscalEntradaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface NotaFiscalEntradaJpaRepository extends JpaRepository<NotaFiscalEntradaJpaEntity, Long> {

    List<NotaFiscalEntradaJpaEntity> findByClienteIdOrderByDataEmissaoDesc(Long clienteId);

    boolean existsByClienteIdAndChaveAcesso(Long clienteId, String chaveAcesso);

    List<NotaFiscalEntradaJpaEntity> findByClienteIdAndDataEmissaoBetweenOrderByDataEmissaoDesc(
            Long clienteId, LocalDate inicio, LocalDate fim);
}
