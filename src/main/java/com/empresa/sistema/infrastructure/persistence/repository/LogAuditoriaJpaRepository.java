package com.empresa.sistema.infrastructure.persistence.repository;

import com.empresa.sistema.infrastructure.persistence.entity.LogAuditoriaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LogAuditoriaJpaRepository extends JpaRepository<LogAuditoriaJpaEntity, Long>,
        JpaSpecificationExecutor<LogAuditoriaJpaEntity> {
}
