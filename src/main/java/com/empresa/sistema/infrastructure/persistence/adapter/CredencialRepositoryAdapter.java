package com.empresa.sistema.infrastructure.persistence.adapter;

import com.empresa.sistema.application.port.out.CredencialRepositoryPort;
import com.empresa.sistema.domain.exception.CredencialNaoEncontradaException;
import com.empresa.sistema.domain.model.Credencial;
import com.empresa.sistema.infrastructure.persistence.entity.CredencialJpaEntity;
import com.empresa.sistema.infrastructure.persistence.mapper.CredencialMapper;
import com.empresa.sistema.infrastructure.persistence.repository.CredencialJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CredencialRepositoryAdapter implements CredencialRepositoryPort {

    private final CredencialJpaRepository credencialJpaRepository;
    private final CredencialMapper credencialMapper;

    public CredencialRepositoryAdapter(CredencialJpaRepository credencialJpaRepository, CredencialMapper credencialMapper) {
        this.credencialJpaRepository = credencialJpaRepository;
        this.credencialMapper = credencialMapper;
    }

    @Override
    public List<Credencial> findByClienteId(Long clienteId) {
        return credencialJpaRepository.findByClienteIdOrderBySistemaAsc(clienteId).stream()
                .map(credencialMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Credencial> findById(Long id) {
        return credencialJpaRepository.findById(id).map(credencialMapper::toDomain);
    }

    @Override
    public Credencial save(Credencial credencial) {
        CredencialJpaEntity entity = credencial.getId() == null
                ? new CredencialJpaEntity()
                : credencialJpaRepository.findById(credencial.getId())
                    .orElseThrow(() -> new CredencialNaoEncontradaException(credencial.getId()));

        credencialMapper.aplicarNaEntidade(credencial, entity);
        CredencialJpaEntity salva = credencialJpaRepository.save(entity);
        return credencialMapper.toDomain(salva);
    }

    @Override
    public void deleteById(Long id) {
        credencialJpaRepository.deleteById(id);
    }
}
