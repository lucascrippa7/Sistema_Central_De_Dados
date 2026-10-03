package com.empresa.sistema.infrastructure.persistence.repository;

import com.empresa.sistema.infrastructure.persistence.entity.ClienteCodigoServicoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteCodigoServicoJpaRepository extends JpaRepository<ClienteCodigoServicoJpaEntity, Long> {
}
