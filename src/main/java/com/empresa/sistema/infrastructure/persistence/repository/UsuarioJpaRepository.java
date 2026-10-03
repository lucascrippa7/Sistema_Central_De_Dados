package com.empresa.sistema.infrastructure.persistence.repository;

import com.empresa.sistema.infrastructure.persistence.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, Long> {
    Optional<UsuarioJpaEntity> findByLogin(String login);
    List<UsuarioJpaEntity> findByAtivoTrueOrderByNomeAsc();
    List<UsuarioJpaEntity> findAllByOrderByNomeAsc();
    boolean existsByLogin(String login);
}
