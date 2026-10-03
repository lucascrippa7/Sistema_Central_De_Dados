package com.empresa.sistema.application.port.out;

import com.empresa.sistema.domain.model.CnaeReferencia;

import java.util.List;

public interface CnaeReferenciaRepositoryPort {
    List<CnaeReferencia> listarTodos();
}
