package com.empresa.sistema.domain.exception;

public class SenhaAtualIncorretaException extends RuntimeException {
    public SenhaAtualIncorretaException() {
        super("A senha atual informada esta incorreta");
    }
}
