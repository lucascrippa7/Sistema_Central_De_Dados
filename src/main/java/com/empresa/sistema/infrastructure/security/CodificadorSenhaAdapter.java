package com.empresa.sistema.infrastructure.security;

import com.empresa.sistema.application.port.out.CodificadorSenhaPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CodificadorSenhaAdapter implements CodificadorSenhaPort {

    private final PasswordEncoder passwordEncoder;

    public CodificadorSenhaAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String codificar(String senhaPlana) {
        return passwordEncoder.encode(senhaPlana);
    }

    @Override
    public boolean confere(String senhaPlana, String hash) {
        return passwordEncoder.matches(senhaPlana, hash);
    }
}
