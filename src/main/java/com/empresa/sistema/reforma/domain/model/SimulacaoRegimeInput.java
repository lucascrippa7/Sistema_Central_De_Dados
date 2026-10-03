package com.empresa.sistema.reforma.domain.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Dados de entrada do simulador (Fase 1). Objeto de dominio puro --
 * sem JPA, nao e persistido nesta fase (ver README do modulo).
 */
@Getter
@Setter
@NoArgsConstructor
public class SimulacaoRegimeInput {

    @NotNull(message = "Informe o setor")
    private SetorEconomico setor;

    @NotNull(message = "Informe o regime tributario atual")
    private RegimeTributario regimeAtual;

    @NotNull(message = "Informe o faturamento anual")
    @DecimalMin(value = "0.01", message = "Faturamento deve ser maior que zero")
    private BigDecimal faturamentoAnual;

    /** Percentual (0 a 100) do faturamento vendido para outras empresas (B2B), que tendem a aproveitar credito. */
    @NotNull(message = "Informe o percentual de vendas B2B")
    @DecimalMin(value = "0.0", message = "Percentual nao pode ser negativo")
    @DecimalMax(value = "100.0", message = "Percentual nao pode passar de 100")
    private BigDecimal percentualVendaB2B;

    /** Opcional -- se o cliente ja tem um id no cadastro de clientes, guardamos so pra referencia no resultado (nao usado no calculo ainda). */
    private Long clienteId;
}
