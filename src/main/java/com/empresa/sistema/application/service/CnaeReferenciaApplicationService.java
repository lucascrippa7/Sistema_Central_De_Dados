package com.empresa.sistema.application.service;

import com.empresa.sistema.application.port.in.CnaeReferenciaUseCase;
import com.empresa.sistema.application.port.out.CnaeReferenciaRepositoryPort;
import com.empresa.sistema.domain.model.CnaeReferencia;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CnaeReferenciaApplicationService implements CnaeReferenciaUseCase {

    private final CnaeReferenciaRepositoryPort cnaeReferenciaRepositoryPort;

    public CnaeReferenciaApplicationService(CnaeReferenciaRepositoryPort cnaeReferenciaRepositoryPort) {
        this.cnaeReferenciaRepositoryPort = cnaeReferenciaRepositoryPort;
    }

    @Override
    public List<CnaeReferencia> listar() {
        return cnaeReferenciaRepositoryPort.listarTodos();
    }
}
