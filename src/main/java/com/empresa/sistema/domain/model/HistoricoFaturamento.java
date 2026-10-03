package com.empresa.sistema.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Faturamento anual informado manualmente pelo usuario -- Fase 4 do modulo
 * de reforma tributaria. Usado pra anos anteriores ao sistema (sem NF-e
 * importada) ou pra corrigir/complementar um ano que ja tem NF-e de saida.
 * Ver HistoricoFaturamentoAno para a visao combinada (manual + calculado).
 */
@Getter
@Setter
@NoArgsConstructor
public class HistoricoFaturamento {
    private Long id;
    private Long clienteId;
    private int ano;
    private BigDecimal valorFaturamento = BigDecimal.ZERO;
    private LocalDateTime atualizadoEm;
    private UsuarioResumo atualizadoPor;
}
