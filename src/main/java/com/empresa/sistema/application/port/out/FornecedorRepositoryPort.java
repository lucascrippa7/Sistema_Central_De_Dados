package com.empresa.sistema.application.port.out;

import com.empresa.sistema.domain.model.Fornecedor;

import java.util.List;
import java.util.Optional;

/**
 * Salva/busca/remove um fornecedor direto, um por vez -- mesmo padrao ja
 * usado por Credencial, Anexo e (depois de corrigido) ClienteCnae. Nunca
 * passa pelo Cliente inteiro.
 */
public interface FornecedorRepositoryPort {
    List<Fornecedor> findByClienteId(Long clienteId);
    Optional<Fornecedor> findById(Long id);
    Fornecedor save(Fornecedor fornecedor);
    void deleteById(Long id);
}
