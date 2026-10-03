package com.empresa.sistema.infrastructure.persistence.repository;

import com.empresa.sistema.infrastructure.persistence.entity.ReducaoAliquotaLc214JpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReducaoAliquotaLc214JpaRepository extends JpaRepository<ReducaoAliquotaLc214JpaEntity, String> {
    List<ReducaoAliquotaLc214JpaEntity> findAllByOrderByCategoriaAsc();
}
