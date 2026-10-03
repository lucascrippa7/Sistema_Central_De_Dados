package com.empresa.sistema.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Uma faixa de reducao de aliquota de IBS/CBS da LC 214/2025 (tabela de
 * referencia, ver migration V14 pra observacoes importantes sobre o
 * recorte usado e a regra de nao cumulatividade). Alimenta o comparativo
 * de regimes tributarios (Fase 4 do modulo de reforma tributaria).
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReducaoAliquotaLc214 {
    private String codigo;
    private String categoria;
    private BigDecimal percentualReducao;
    private String baseLegal;
    private String descricao;
}
