package com.empresa.sistema.domain.exception;

public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException(String login) {
        super("Usuario nao encontrado: " + login);
    }

    public UsuarioNaoEncontradoException(Long id) {
        super("Usuario nao encontrado: " + id);
    }
}
