package com.empresa.sistema.domain.exception;

public class EventoNaoEncontradoException extends RuntimeException {
    public EventoNaoEncontradoException(Long id) {
        super("Evento nao encontrado: " + id);
    }
}
