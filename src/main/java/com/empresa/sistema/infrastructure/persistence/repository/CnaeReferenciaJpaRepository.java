package com.empresa.sistema.infrastructure.persistence.repository;

import com.empresa.sistema.infrastructure.persistence.entity.CnaeReferenciaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CnaeReferenciaJpaRepository extends JpaRepository<CnaeReferenciaJpaEntity, String> {
    List<CnaeReferenciaJpaEntity> findAllByOrderByCodigoAsc();
}
