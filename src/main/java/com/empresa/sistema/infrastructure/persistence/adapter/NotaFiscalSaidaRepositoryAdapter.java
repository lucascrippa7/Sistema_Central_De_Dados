package com.empresa.sistema.infrastructure.persistence.adapter;

import com.empresa.sistema.application.port.out.NotaFiscalSaidaRepositoryPort;
import com.empresa.sistema.domain.model.NotaFiscalSaida;
import com.empresa.sistema.infrastructure.persistence.mapper.NotaFiscalSaidaMapper;
import com.empresa.sistema.infrastructure.persistence.repository.NotaFiscalSaidaJpaRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class NotaFiscalSaidaRepositoryAdapter implements NotaFiscalSaidaRepositoryPort {

    private final NotaFiscalSaidaJpaRepository jpaRepository;
    private final NotaFiscalSaidaMapper mapper;

    public NotaFiscalSaidaRepositoryAdapter(NotaFiscalSaidaJpaRepository jpaRepository, NotaFiscalSaidaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public NotaFiscalSaida save(NotaFiscalSaida notaFiscalSaida) {
        return mapper.toDomain(jpaRepository.save(mapper.paraNovaEntidade(notaFiscalSaida)));
    }

    @Override
    public Optional<NotaFiscalSaida> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByClienteIdAndChaveAcesso(Long clienteId, String chaveAcesso) {
        return jpaRepository.existsByClienteIdAndChaveAcesso(clienteId, chaveAcesso);
    }

    @Override
    public List<NotaFiscalSaida> findByClienteId(Long clienteId) {
        return jpaRepository.findByClienteIdOrderByDataEmissaoDesc(clienteId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Integer, BigDecimal> somarValorTotalPorAno(Long clienteId) {
        Map<Integer, BigDecimal> resultado = new HashMap<>();
        for (Object[] linha : jpaRepository.somarValorTotalPorAno(clienteId)) {
            int ano = ((Number) linha[0]).intValue();
            BigDecimal total = (BigDecimal) linha[1];
            resultado.put(ano, total != null ? total : BigDecimal.ZERO);
        }
        return resultado;
    }

    @Override
    public Map<Integer, Integer> contarPorAno(Long clienteId) {
        Map<Integer, Integer> resultado = new HashMap<>();
        for (Object[] linha : jpaRepository.contarPorAno(clienteId)) {
            int ano = ((Number) linha[0]).intValue();
            int quantidade = ((Number) linha[1]).intValue();
            resultado.put(ano, quantidade);
        }
        return resultado;
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
