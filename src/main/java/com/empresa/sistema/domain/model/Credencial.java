package com.empresa.sistema.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Credencial de acesso a um portal externo (Giss Online, Ginfes, Nota do
 * Milhao, etc.) de um cliente.
 *
 * O campo "senha" so vem preenchido quando explicitamente solicitado (via
 * revelarSenha) -- em qualquer listagem normal, ele vem nulo de proposito.
 * Isso e decidido em CredencialApplicationService, nao aqui no dominio.
 */
@Getter
@Setter
@NoArgsConstructor
public class Credencial {
    private Long id;
    private Long clienteId;

    /** Ex: "Giss Online", "Ginfes", "Nota do Milhao" */
    private String sistema;

    private String urlPortal;
    private String usuarioPortal;
    private String senha;
    private String observacoes;

    private LocalDateTime criadoEm;
    private UsuarioResumo criadoPor;
    private LocalDateTime atualizadoEm;
    private UsuarioResumo atualizadoPor;
}
