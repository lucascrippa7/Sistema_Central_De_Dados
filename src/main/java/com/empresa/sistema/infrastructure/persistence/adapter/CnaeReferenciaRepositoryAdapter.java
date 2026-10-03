package com.empresa.sistema.infrastructure.persistence.adapter;

import com.empresa.sistema.application.port.out.CnaeReferenciaRepositoryPort;
import com.empresa.sistema.domain.model.CnaeReferencia;
import com.empresa.sistema.infrastructure.persistence.repository.CnaeReferenciaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CnaeReferenciaRepositoryAdapter implements CnaeReferenciaRepositoryPort {

    private final CnaeReferenciaJpaRepository cnaeReferenciaJpaRepository;

    public CnaeReferenciaRepositoryAdapter(CnaeReferenciaJpaRepository cnaeReferenciaJpaRepository) {
        this.cnaeReferenciaJpaRepository = cnaeReferenciaJpaRepository;
    }

    @Override
    public List<CnaeReferencia> listarTodos() {
        return cnaeReferenciaJpaRepository.findAllByOrderByCodigoAsc().stream()
                .map(e -> new CnaeReferencia(e.getCodigo(), e.getDescricao()))
                .collect(Collectors.toList());
    }
}
