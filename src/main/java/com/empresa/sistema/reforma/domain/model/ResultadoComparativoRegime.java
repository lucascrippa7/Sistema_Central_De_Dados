package com.empresa.sistema.reforma.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Resultado completo do comparativo de regimes tributarios de um cliente, para um ano especifico -- Fase 4 da reforma tributaria. */
@Getter
@Setter
@NoArgsConstructor
public class ResultadoComparativoRegime {
    private int ano;
    private BigDecimal faturamentoUsado = BigDecimal.ZERO;
    private BigDecimal creditoIcmsUsado = BigDecimal.ZERO;
    private BigDecimal creditoPisUsado = BigDecimal.ZERO;
    private BigDecimal creditoCofinsUsado = BigDecimal.ZERO;
    private String categoriaReducaoUsada;
    private List<RegimeCalculado> regimes = new ArrayList<>();
    private String melhorRegimeNome;
    private String aviso;
}
