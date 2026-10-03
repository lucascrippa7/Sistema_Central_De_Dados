package com.empresa.sistema.application.port.out;

import com.empresa.sistema.domain.model.Credencial;

import java.util.List;
import java.util.Optional;

public interface CredencialRepositoryPort {
    List<Credencial> findByClienteId(Long clienteId);
    Optional<Credencial> findById(Long id);
    Credencial save(Credencial credencial);
    void deleteById(Long id);
}
