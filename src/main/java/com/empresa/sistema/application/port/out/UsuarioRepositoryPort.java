package com.empresa.sistema.application.port.out;

import com.empresa.sistema.domain.model.Usuario;
import com.empresa.sistema.domain.model.UsuarioResumo;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositoryPort {
    Optional<Usuario> findByLogin(String login);
    List<UsuarioResumo> findAllAtivosResumo();
    List<Usuario> findAll();
    Optional<Usuario> findById(Long id);
    boolean existsByLogin(String login);
    Usuario save(Usuario usuario);
}
