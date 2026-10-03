package com.empresa.sistema.domain.exception;

/**
 * Lancada quando um funcionario tenta ver, editar ou excluir um evento que
 * nao e dele e que ele nao tem permissao de administrador para acessar.
 */
public class AcessoNegadoException extends RuntimeException {
    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
