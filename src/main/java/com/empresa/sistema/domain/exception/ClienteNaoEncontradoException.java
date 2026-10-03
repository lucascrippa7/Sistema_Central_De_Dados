package com.empresa.sistema.domain.exception;

public class ClienteNaoEncontradoException extends RuntimeException {
    public ClienteNaoEncontradoException(Long id) {
        super("Cliente nao encontrado: " + id);
    }
}
