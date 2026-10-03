package com.empresa.sistema.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Resumo de credito de ICMS/PIS/COFINS de um cliente em um periodo -- Fase 3 da reforma tributaria. */
@Getter
@Setter
@NoArgsConstructor
public class ResumoCreditoTributario {
    private LocalDate periodoInicio;
    private LocalDate periodoFim;
    private int quantidadeNotas;
    private BigDecimal totalCreditoIcms = BigDecimal.ZERO;
    private BigDecimal totalCreditoPis = BigDecimal.ZERO;
    private BigDecimal totalCreditoCofins = BigDecimal.ZERO;

    public BigDecimal getTotalCreditoGeral() {
        return totalCreditoIcms.add(totalCreditoPis).add(totalCreditoCofins);
    }
}
