package com.empresa.sistema.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Referencia leve a um usuario (id + nome + login), usada quando outras
 * entidades de dominio (Cliente, LogAuditoria) precisam apontar "quem fez
 * isso" ou "quem e o responsavel" sem carregar o Usuario inteiro -- em
 * especial, sem carregar o hash da senha.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResumo {
    private Long id;
    private String nome;
    private String login;
}
