package com.empresa.sistema.application.port.in;

import com.empresa.sistema.domain.model.CnaeReferencia;

import java.util.List;

public interface CnaeReferenciaUseCase {
    /** Toda a tabela de referencia, ordenada por codigo -- usada pra alimentar a lista de sugestao. */
    List<CnaeReferencia> listar();
}
