package com.empresa.sistema.infrastructure.persistence.repository;

import com.empresa.sistema.infrastructure.persistence.entity.CredencialJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CredencialJpaRepository extends JpaRepository<CredencialJpaEntity, Long> {
    List<CredencialJpaEntity> findByClienteIdOrderBySistemaAsc(Long clienteId);
}
