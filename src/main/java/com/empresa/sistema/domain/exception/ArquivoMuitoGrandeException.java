package com.empresa.sistema.domain.exception;

public class ArquivoMuitoGrandeException extends RuntimeException {
    public ArquivoMuitoGrandeException(long limiteBytes) {
        super("Arquivo maior que o limite permitido (" + (limiteBytes / (1024 * 1024)) + " MB)");
    }
}
