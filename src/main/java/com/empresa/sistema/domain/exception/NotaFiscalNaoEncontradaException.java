package com.empresa.sistema.domain.exception;

public class NotaFiscalNaoEncontradaException extends RuntimeException {
    public NotaFiscalNaoEncontradaException(Long id) {
        super("Nota fiscal nao encontrada: " + id);
    }
}
