package com.empresa.sistema.application.port.in;

import com.empresa.sistema.domain.model.HistoricoFaturamentoAno;
import com.empresa.sistema.domain.model.UsuarioResumo;

import java.math.BigDecimal;
import java.util.List;

public interface HistoricoFaturamentoUseCase {

    /**
     * Faturamento combinado (manual + NF-e de saida) por ano, do ano mais antigo
     * com algum dado ate o ano atual -- pronto pra exibir numa tabela/grafico.
     */
    List<HistoricoFaturamentoAno> listarPorCliente(Long clienteId);

    /** Informa/corrige manualmente o faturamento de um ano -- passar valor null remove o registro manual (volta a valer o calculado via NF-e). */
    void definirManual(Long clienteId, int ano, BigDecimal valor, UsuarioResumo usuarioLogado);
}
