package com.empresa.sistema.application.port.out;

/**
 * Porta de saida para codificar e conferir senhas. A aplicacao pede
 * "codifique essa senha" ou "essa senha confere com esse hash" sem saber
 * (nem precisar saber) que por baixo isso e BCrypt -- essa escolha de
 * algoritmo fica isolada na infraestrutura.
 */
public interface CodificadorSenhaPort {
    String codificar(String senhaPura);
    boolean confere(String senhaPura, String hash);
}
