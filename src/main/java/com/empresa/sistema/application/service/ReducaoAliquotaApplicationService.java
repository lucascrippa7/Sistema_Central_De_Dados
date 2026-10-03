package com.empresa.sistema.application.service;

import com.empresa.sistema.application.port.in.ReducaoAliquotaUseCase;
import com.empresa.sistema.application.port.out.ReducaoAliquotaRepositoryPort;
import com.empresa.sistema.domain.model.ReducaoAliquotaLc214;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReducaoAliquotaApplicationService implements ReducaoAliquotaUseCase {

    private final ReducaoAliquotaRepositoryPort reducaoAliquotaRepositoryPort;

    public ReducaoAliquotaApplicationService(ReducaoAliquotaRepositoryPort reducaoAliquotaRepositoryPort) {
        this.reducaoAliquotaRepositoryPort = reducaoAliquotaRepositoryPort;
    }

    @Override
    public List<ReducaoAliquotaLc214> listar() {
        return reducaoAliquotaRepositoryPort.listarTodos();
    }
}
