package com.empresa.sistema.domain.exception;

public class ClienteObrigatorioException extends RuntimeException {
    public ClienteObrigatorioException() {
        super("Selecione um cliente: evento do tipo Entrega de imposto precisa estar vinculado a um cliente");
    }
}
