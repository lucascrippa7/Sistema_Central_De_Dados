package com.empresa.sistema.reforma.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Resultado do calculo de um regime especifico dentro do comparativo (Fase 4). */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegimeCalculado {
    private String nome;
    private BigDecimal cargaTributariaEstimada;
    private BigDecimal percentualSobreFaturamento;
    private String detalhamento;
}
