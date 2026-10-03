package com.empresa.sistema.infrastructure.persistence.adapter;

import com.empresa.sistema.application.port.out.FornecedorRepositoryPort;
import com.empresa.sistema.domain.exception.FornecedorNaoEncontradoException;
import com.empresa.sistema.domain.model.Fornecedor;
import com.empresa.sistema.infrastructure.persistence.entity.FornecedorJpaEntity;
import com.empresa.sistema.infrastructure.persistence.mapper.FornecedorMapper;
import com.empresa.sistema.infrastructure.persistence.repository.FornecedorJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FornecedorRepositoryAdapter implements FornecedorRepositoryPort {

    private final FornecedorJpaRepository fornecedorJpaRepository;
    private final FornecedorMapper fornecedorMapper;

    public FornecedorRepositoryAdapter(FornecedorJpaRepository fornecedorJpaRepository, FornecedorMapper fornecedorMapper) {
        this.fornecedorJpaRepository = fornecedorJpaRepository;
        this.fornecedorMapper = fornecedorMapper;
    }

    @Override
    public List<Fornecedor> findByClienteId(Long clienteId) {
        return fornecedorJpaRepository.findByClienteIdOrderByRazaoSocialAsc(clienteId).stream()
                .map(fornecedorMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Fornecedor> findById(Long id) {
        return fornecedorJpaRepository.findById(id).map(fornecedorMapper::toDomain);
    }

    @Override
    public Fornecedor save(Fornecedor fornecedor) {
        FornecedorJpaEntity entity;
        if (fornecedor.getId() == null) {
            entity = fornecedorMapper.paraNovaEntidade(fornecedor);
        } else {
            entity = fornecedorJpaRepository.findById(fornecedor.getId())
                    .orElseThrow(() -> new FornecedorNaoEncontradoException(fornecedor.getId()));
            fornecedorMapper.aplicarNaEntidade(fornecedor, entity);
        }
        return fornecedorMapper.toDomain(fornecedorJpaRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        fornecedorJpaRepository.deleteById(id);
    }
}
