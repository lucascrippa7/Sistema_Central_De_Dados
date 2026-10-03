package com.empresa.sistema.application.port.out;

import com.empresa.sistema.domain.model.ReducaoAliquotaLc214;

import java.util.List;

public interface ReducaoAliquotaRepositoryPort {
    List<ReducaoAliquotaLc214> listarTodos();
}
