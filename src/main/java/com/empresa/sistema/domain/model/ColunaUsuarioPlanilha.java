package com.empresa.sistema.domain.model;

/**
 * Colunas OPCIONAIS que podem entrar numa planilha de usuarios exportada.
 * "Nome" nao esta na lista porque e sempre incluido.
 */
public enum ColunaUsuarioPlanilha {
    LOGIN("Login"),
    CARGO("Cargo"),
    TIPO_ACESSO("Tipo de Acesso"),
    STATUS("Ativo");

    private final String rotulo;

    ColunaUsuarioPlanilha(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
