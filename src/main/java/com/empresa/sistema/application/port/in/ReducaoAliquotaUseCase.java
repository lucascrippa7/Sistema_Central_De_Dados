package com.empresa.sistema.application.port.in;

import com.empresa.sistema.domain.model.ReducaoAliquotaLc214;

import java.util.List;

public interface ReducaoAliquotaUseCase {
    /** Toda a tabela de referencia da LC 214/2025, ordenada por categoria. */
    List<ReducaoAliquotaLc214> listar();
}
