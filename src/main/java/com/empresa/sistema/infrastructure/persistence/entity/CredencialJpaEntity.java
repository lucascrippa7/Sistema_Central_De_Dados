package com.empresa.sistema.infrastructure.persistence.entity;

import com.empresa.sistema.infrastructure.persistence.converter.CriptografiaConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "credenciais")
@Getter
@Setter
@NoArgsConstructor
public class CredencialJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteJpaEntity cliente;

    @Column(nullable = false, length = 100)
    private String sistema;

    @Column(name = "url_portal", length = 300)
    private String urlPortal;

    @Column(name = "usuario_portal", length = 150)
    private String usuarioPortal;

    // Criptografado/descriptografado automaticamente (AES-GCM) -- ver CriptografiaConverter.
    @Convert(converter = CriptografiaConverter.class)
    @Column(name = "senha_criptografada", length = 500)
    private String senha;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "criado_por_id")
    private UsuarioJpaEntity criadoPor;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @ManyToOne
    @JoinColumn(name = "atualizado_por_id")
    private UsuarioJpaEntity atualizadoPor;
}
