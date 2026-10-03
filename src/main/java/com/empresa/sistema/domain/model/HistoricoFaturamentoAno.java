package com.empresa.sistema.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Visao combinada do faturamento de um cliente em um ano -- Fase 4 do
 * modulo de reforma tributaria. Nao e persistida: e montada juntando o
 * valor informado manualmente (HistoricoFaturamento) com a soma das NF-e
 * de saida importadas naquele ano (NotaFiscalSaida).
 *
 * Regra de qual valor "vale": se o usuario informou manualmente, o manual
 * prevalece (ele pode ter corrigido algo que o calculo automatico nao pega,
 * tipo receita de servico sem nota ou nota cancelada fora do XML) -- senao,
 * usa o calculado via NF-e.
 */
@Getter
@Setter
@NoArgsConstructor
public class HistoricoFaturamentoAno {
    private int ano;
    private BigDecimal valorManual;
    private BigDecimal valorCalculadoNfe = BigDecimal.ZERO;
    private int quantidadeNotasSaida;

    public BigDecimal getValorConsiderado() {
        return valorManual != null ? valorManual : valorCalculadoNfe;
    }

    public String getOrigem() {
        return valorManual != null ? "MANUAL" : "NF-E";
    }
}
