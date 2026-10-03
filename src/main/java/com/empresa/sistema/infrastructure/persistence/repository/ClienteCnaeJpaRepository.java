package com.empresa.sistema.infrastructure.persistence.repository;

import com.empresa.sistema.infrastructure.persistence.entity.ClienteCnaeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteCnaeJpaRepository extends JpaRepository<ClienteCnaeJpaEntity, Long> {
}
