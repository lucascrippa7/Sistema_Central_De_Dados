package com.empresa.sistema.domain.exception;

public class FornecedorNaoEncontradoException extends RuntimeException {
    public FornecedorNaoEncontradoException(Long id) {
        super("Fornecedor nao encontrado: " + id);
    }
}
