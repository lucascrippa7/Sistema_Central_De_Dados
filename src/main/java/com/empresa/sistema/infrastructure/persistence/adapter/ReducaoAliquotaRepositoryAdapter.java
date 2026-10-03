package com.empresa.sistema.infrastructure.persistence.adapter;

import com.empresa.sistema.application.port.out.ReducaoAliquotaRepositoryPort;
import com.empresa.sistema.domain.model.ReducaoAliquotaLc214;
import com.empresa.sistema.infrastructure.persistence.repository.ReducaoAliquotaLc214JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReducaoAliquotaRepositoryAdapter implements ReducaoAliquotaRepositoryPort {

    private final ReducaoAliquotaLc214JpaRepository jpaRepository;

    public ReducaoAliquotaRepositoryAdapter(ReducaoAliquotaLc214JpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<ReducaoAliquotaLc214> listarTodos() {
        return jpaRepository.findAllByOrderByCategoriaAsc().stream()
                .map(e -> new ReducaoAliquotaLc214(e.getCodigo(), e.getCategoria(), e.getPercentualReducao(), e.getBaseLegal(), e.getDescricao()))
                .collect(Collectors.toList());
    }
}
