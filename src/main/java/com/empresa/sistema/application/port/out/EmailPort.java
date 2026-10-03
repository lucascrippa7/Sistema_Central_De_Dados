package com.empresa.sistema.application.port.out;

public interface EmailPort {
    void enviar(String destinatario, String assunto, String corpo);
}
