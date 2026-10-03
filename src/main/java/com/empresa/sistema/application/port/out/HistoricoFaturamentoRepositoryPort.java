package com.empresa.sistema.application.port.out;

import com.empresa.sistema.domain.model.HistoricoFaturamento;

import java.util.List;
import java.util.Optional;

public interface HistoricoFaturamentoRepositoryPort {

    HistoricoFaturamento save(HistoricoFaturamento historicoFaturamento);

    Optional<HistoricoFaturamento> findByClienteIdAndAno(Long clienteId, int ano);

    List<HistoricoFaturamento> findByClienteId(Long clienteId);

    void deleteByClienteIdAndAno(Long clienteId, int ano);
}
