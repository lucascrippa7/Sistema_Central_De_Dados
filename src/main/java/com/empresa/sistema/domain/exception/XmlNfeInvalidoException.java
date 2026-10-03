package com.empresa.sistema.domain.exception;

/** Lancada quando um arquivo enviado nao e um XML de NF-e valido (estrutura inesperada, nao e NF-e, etc). */
public class XmlNfeInvalidoException extends RuntimeException {
    public XmlNfeInvalidoException(String motivo) {
        super(motivo);
    }
}
