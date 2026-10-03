package com.empresa.sistema.infrastructure.persistence.repository;

import com.empresa.sistema.infrastructure.persistence.entity.FornecedorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FornecedorJpaRepository extends JpaRepository<FornecedorJpaEntity, Long> {
    List<FornecedorJpaEntity> findByClienteIdOrderByRazaoSocialAsc(Long clienteId);
}
