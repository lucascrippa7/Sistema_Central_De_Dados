package com.empresa.sistema.infrastructure.persistence.adapter;

import com.empresa.sistema.application.port.out.HistoricoFaturamentoRepositoryPort;
import com.empresa.sistema.domain.model.HistoricoFaturamento;
import com.empresa.sistema.infrastructure.persistence.entity.HistoricoFaturamentoJpaEntity;
import com.empresa.sistema.infrastructure.persistence.mapper.HistoricoFaturamentoMapper;
import com.empresa.sistema.infrastructure.persistence.repository.HistoricoFaturamentoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class HistoricoFaturamentoRepositoryAdapter implements HistoricoFaturamentoRepositoryPort {

    private final HistoricoFaturamentoJpaRepository jpaRepository;
    private final HistoricoFaturamentoMapper mapper;

    public HistoricoFaturamentoRepositoryAdapter(HistoricoFaturamentoJpaRepository jpaRepository, HistoricoFaturamentoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public HistoricoFaturamento save(HistoricoFaturamento historicoFaturamento) {
        HistoricoFaturamentoJpaEntity entity;
        if (historicoFaturamento.getId() == null) {
            entity = mapper.paraNovaEntidade(historicoFaturamento);
        } else {
            entity = jpaRepository.findById(historicoFaturamento.getId())
                    .orElseThrow(() -> new IllegalStateException("Historico de faturamento nao encontrado: " + historicoFaturamento.getId()));
            mapper.aplicarNaEntidade(historicoFaturamento, entity);
        }
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<HistoricoFaturamento> findByClienteIdAndAno(Long clienteId, int ano) {
        return jpaRepository.findByClienteIdAndAno(clienteId, ano).map(mapper::toDomain);
    }

    @Override
    public List<HistoricoFaturamento> findByClienteId(Long clienteId) {
        return jpaRepository.findByClienteIdOrderByAnoDesc(clienteId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByClienteIdAndAno(Long clienteId, int ano) {
        jpaRepository.deleteByClienteIdAndAno(clienteId, ano);
    }
}
