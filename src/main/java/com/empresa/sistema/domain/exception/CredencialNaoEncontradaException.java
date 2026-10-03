package com.empresa.sistema.domain.exception;

public class CredencialNaoEncontradaException extends RuntimeException {
    public CredencialNaoEncontradaException(Long id) {
        super("Credencial nao encontrada: " + id);
    }
}
