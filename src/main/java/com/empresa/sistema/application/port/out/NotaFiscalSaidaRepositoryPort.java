package com.empresa.sistema.application.port.out;

import com.empresa.sistema.domain.model.NotaFiscalSaida;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface NotaFiscalSaidaRepositoryPort {

    NotaFiscalSaida save(NotaFiscalSaida notaFiscalSaida);

    Optional<NotaFiscalSaida> findById(Long id);

    boolean existsByClienteIdAndChaveAcesso(Long clienteId, String chaveAcesso);

    List<NotaFiscalSaida> findByClienteId(Long clienteId);

    /** Soma de valorTotalNota por ano (ano de dataEmissao), pra montar HistoricoFaturamentoAno sem carregar nota por nota. */
    Map<Integer, BigDecimal> somarValorTotalPorAno(Long clienteId);

    /** Quantidade de notas por ano, mesma finalidade do metodo acima. */
    Map<Integer, Integer> contarPorAno(Long clienteId);

    void deleteById(Long id);
}
