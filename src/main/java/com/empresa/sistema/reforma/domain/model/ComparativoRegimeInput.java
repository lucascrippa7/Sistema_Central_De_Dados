package com.empresa.sistema.reforma.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Parametros que o comparativo precisa e o sistema NAO tem como calcular
 * sozinho a partir dos dados ja cadastrados -- pedidos no formulario da
 * tela (Fase 4 do modulo de reforma tributaria).
 */
@Getter
@Setter
@NoArgsConstructor
public class ComparativoRegimeInput {

    private SetorEconomico setor = SetorEconomico.SERVICOS;

    /** Aliquota de ISS do municipio (%) -- so usada quando o setor e SERVICOS; varia de 2% a 5% conforme o municipio. */
    private BigDecimal aliquotaIss = new BigDecimal("5.00");

    /** Margem de lucro estimada (%) sobre o faturamento -- usada so pro Lucro Real, ja que o sistema nao acompanha despesas/lucro contabil real do cliente. */
    private BigDecimal margemLucroEstimada = new BigDecimal("10.00");
}
