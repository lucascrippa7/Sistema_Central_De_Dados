package com.empresa.sistema.domain.exception;

public class LoginJaExisteException extends RuntimeException {
    public LoginJaExisteException(String login) {
        super("Ja existe um usuario com o login: " + login);
    }
}
