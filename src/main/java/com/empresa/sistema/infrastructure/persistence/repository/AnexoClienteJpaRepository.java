package com.empresa.sistema.infrastructure.persistence.repository;

import com.empresa.sistema.infrastructure.persistence.entity.AnexoClienteJpaEntity;
import com.empresa.sistema.infrastructure.persistence.projection.AnexoResumoProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnexoClienteJpaRepository extends JpaRepository<AnexoClienteJpaEntity, Long> {

    /** Sem o conteudo -- usado pra listar (ver AnexoResumoProjection). */
    List<AnexoResumoProjection> findByClienteIdOrderByCriadoEmDesc(Long clienteId);
}
