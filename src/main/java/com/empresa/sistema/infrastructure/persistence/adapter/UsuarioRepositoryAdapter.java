package com.empresa.sistema.infrastructure.persistence.adapter;

import com.empresa.sistema.application.port.out.UsuarioRepositoryPort;
import com.empresa.sistema.domain.exception.UsuarioNaoEncontradoException;
import com.empresa.sistema.domain.model.Usuario;
import com.empresa.sistema.domain.model.UsuarioResumo;
import com.empresa.sistema.infrastructure.persistence.entity.UsuarioJpaEntity;
import com.empresa.sistema.infrastructure.persistence.mapper.UsuarioMapper;
import com.empresa.sistema.infrastructure.persistence.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository usuarioJpaRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository usuarioJpaRepository, UsuarioMapper usuarioMapper) {
        this.usuarioJpaRepository = usuarioJpaRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public Optional<Usuario> findByLogin(String login) {
        return usuarioJpaRepository.findByLogin(login).map(usuarioMapper::toDomain);
    }

    @Override
    public List<UsuarioResumo> findAllAtivosResumo() {
        return usuarioJpaRepository.findByAtivoTrueOrderByNomeAsc().stream()
                .map(usuarioMapper::toResumo)
                .collect(Collectors.toList());
    }

    @Override
    public List<Usuario> findAll() {
        return usuarioJpaRepository.findAllByOrderByNomeAsc().stream()
                .map(usuarioMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioJpaRepository.findById(id).map(usuarioMapper::toDomain);
    }

    @Override
    public boolean existsByLogin(String login) {
        return usuarioJpaRepository.existsByLogin(login);
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioJpaEntity entity;
        if (usuario.getId() == null) {
            entity = new UsuarioJpaEntity();
        } else {
            entity = usuarioJpaRepository.findById(usuario.getId())
                    .orElseThrow(() -> new UsuarioNaoEncontradoException(usuario.getId()));
        }

        usuarioMapper.aplicarNaEntidade(usuario, entity);
        UsuarioJpaEntity salvo = usuarioJpaRepository.save(entity);
        return usuarioMapper.toDomain(salvo);
    }
}
