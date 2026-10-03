package com.empresa.sistema.application.port.out;

import com.empresa.sistema.domain.model.AnexoCliente;

import java.util.List;
import java.util.Optional;

public interface AnexoClienteRepositoryPort {

    /** Sem o campo "conteudo" preenchido -- so metadados, pra listagem. */
    List<AnexoCliente> findResumoPorClienteId(Long clienteId);

    /** Com o campo "conteudo" preenchido -- usado so na hora de baixar. */
    Optional<AnexoCliente> findComConteudoById(Long id);

    AnexoCliente save(AnexoCliente anexo);

    void deleteById(Long id);
}
