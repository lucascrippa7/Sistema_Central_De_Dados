package com.empresa.sistema.reforma.domain.model;

/**
 * Setor economico do cliente, usado para escolher o anexo do Simples
 * Nacional na simulacao (ver AnexoSimples) e para ajustar a estimativa
 * de carga no regime atual.
 */
public enum SetorEconomico {
    COMERCIO,
    INDUSTRIA,
    SERVICOS
}
