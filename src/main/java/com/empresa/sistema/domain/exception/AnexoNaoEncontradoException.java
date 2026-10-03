package com.empresa.sistema.domain.exception;

public class AnexoNaoEncontradoException extends RuntimeException {
    public AnexoNaoEncontradoException(Long id) {
        super("Anexo nao encontrado: " + id);
    }
}
