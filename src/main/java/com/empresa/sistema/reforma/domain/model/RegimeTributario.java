package com.empresa.sistema.reforma.domain.model;

/**
 * Regime tributario atual do cliente, para fins da simulacao.
 * Note que isso e um enum proprio do modulo de reforma -- o Cliente
 * (fora deste pacote) guarda o regime como String livre no formulario
 * de cadastro, entao o controller precisa mapear a String pro enum
 * antes de simular (ver SimulacaoRegimeController).
 */
public enum RegimeTributario {
    SIMPLES_NACIONAL,
    LUCRO_PRESUMIDO,
    LUCRO_REAL
}
