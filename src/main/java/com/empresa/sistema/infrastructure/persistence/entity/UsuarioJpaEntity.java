package com.empresa.sistema.infrastructure.persistence.entity;

import com.empresa.sistema.domain.model.TipoAcesso;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidade JPA de Usuario. Fica isolada aqui na infraestrutura -- o resto
 * do sistema (aplicacao, dominio) nunca importa esta classe, so o
 * UsuarioMapper e o UsuarioRepositoryAdapter sabem que ela existe.
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class UsuarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 60)
    private String login;

    @Column(length = 150)
    private String email;

    @Column(name = "senha_hash", nullable = false, length = 100)
    private String senhaHash;

    @Column(length = 100)
    private String cargo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_acesso", nullable = false, length = 20)
    private TipoAcesso tipoAcesso;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
}
