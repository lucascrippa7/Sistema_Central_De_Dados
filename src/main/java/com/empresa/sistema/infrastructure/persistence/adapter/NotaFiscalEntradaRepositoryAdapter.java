package com.empresa.sistema.infrastructure.persistence.adapter;

import com.empresa.sistema.application.port.out.NotaFiscalEntradaRepositoryPort;
import com.empresa.sistema.domain.model.NotaFiscalEntrada;
import com.empresa.sistema.infrastructure.persistence.mapper.NotaFiscalEntradaMapper;
import com.empresa.sistema.infrastructure.persistence.repository.NotaFiscalEntradaJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class NotaFiscalEntradaRepositoryAdapter implements NotaFiscalEntradaRepositoryPort {

    private final NotaFiscalEntradaJpaRepository jpaRepository;
    private final NotaFiscalEntradaMapper mapper;

    public NotaFiscalEntradaRepositoryAdapter(NotaFiscalEntradaJpaRepository jpaRepository, NotaFiscalEntradaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public NotaFiscalEntrada save(NotaFiscalEntrada notaFiscalEntrada) {
        // Nota fiscal importada nunca e editada, so criada -- por isso so ha o caminho de nova entidade aqui.
        return mapper.toDomain(jpaRepository.save(mapper.paraNovaEntidade(notaFiscalEntrada)));
    }

    @Override
    public Optional<NotaFiscalEntrada> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByClienteIdAndChaveAcesso(Long clienteId, String chaveAcesso) {
        return jpaRepository.existsByClienteIdAndChaveAcesso(clienteId, chaveAcesso);
    }

    @Override
    public List<NotaFiscalEntrada> findByClienteId(Long clienteId) {
        return jpaRepository.findByClienteIdOrderByDataEmissaoDesc(clienteId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotaFiscalEntrada> findByClienteIdAndPeriodo(Long clienteId, LocalDate inicio, LocalDate fim) {
        return jpaRepository.findByClienteIdAndDataEmissaoBetweenOrderByDataEmissaoDesc(clienteId, inicio, fim).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
