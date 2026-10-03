package com.empresa.sistema.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Usuario do sistema, como objeto de dominio puro.
 *
 * IMPORTANTE: esta classe NAO implementa UserDetails do Spring Security.
 * Isso e de proposito: o dominio nao pode depender de uma classe de
 * framework. Quem faz essa "ponte" para o Spring Security e a classe
 * UsuarioAutenticado, la em infrastructure/security.
 */
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    private Long id;

    @NotBlank(message = "Informe o nome")
    private String nome;

    @NotBlank(message = "Informe o login")
    private String login;

    /** Usado so para enviar notificacoes por e-mail (lembretes de eventos). Pode ficar em branco. */
    private String email;

    private String senhaHash;
    private String cargo;

    @NotNull(message = "Selecione o tipo de acesso")
    private TipoAcesso tipoAcesso;

    private boolean ativo = true;
    private LocalDateTime criadoEm;

    /**
     * Senha em TEXTO PURO, usada so no momento de criar o usuario (o
     * formulario de cadastro manda ela aqui). NUNCA e persistida --
     * o UsuarioMapper nao sabe que este campo existe, e a aplicacao
     * transforma ela em senhaHash antes de salvar. Depois de usada,
     * nao deve ser reaproveitada.
     */
    private String senha;

    public UsuarioResumo paraResumo() {
        return new UsuarioResumo(id, nome, login);
    }
}
