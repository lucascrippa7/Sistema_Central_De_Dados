package com.empresa.sistema.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "anexos_cliente")
@Getter
@Setter
@NoArgsConstructor
public class AnexoClienteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteJpaEntity cliente;

    @Column(name = "nome_arquivo", nullable = false, length = 255)
    private String nomeArquivo;

    @Column(name = "tipo_conteudo", length = 100)
    private String tipoConteudo;

    @Column(name = "tamanho_bytes", nullable = false)
    private long tamanhoBytes;

    // Sem @Lob de proposito: no Postgres, byte[] simples ja mapeia pra bytea
    // (o tipo certo pra isso). @Lob puxaria a API de Large Object (oid),
    // que e outra coisa e complica sem necessidade pra arquivos desse tamanho.
    @Column(name = "conteudo", nullable = false)
    private byte[] conteudo;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "criado_por_id")
    private UsuarioJpaEntity criadoPor;
}
