package com.empresa.sistema.reforma.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Resultado da simulacao -- carga tributaria estimada, no regime atual
 * e projetada apos a reforma (CBS/IBS), com uma recomendacao textual.
 * Percentuais sao sobre a receita bruta (ex: 15.00 = 15%).
 */
@Getter
@Setter
@NoArgsConstructor
public class ResultadoSimulacaoRegime {

    private BigDecimal cargaTributariaAtual;
    private BigDecimal cargaTributariaProjetada;
    private BigDecimal diferencaPercentual;
    private String recomendacao;
}
