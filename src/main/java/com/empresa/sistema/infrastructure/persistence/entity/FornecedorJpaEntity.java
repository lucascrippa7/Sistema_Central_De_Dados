package com.empresa.sistema.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "fornecedores")
@Getter
@Setter
@NoArgsConstructor
public class FornecedorJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteJpaEntity cliente;

    @Column(nullable = false, length = 18)
    private String cnpj;

    @Column(name = "razao_social", length = 200)
    private String razaoSocial;

    @Column(name = "regime_tributario", length = 60)
    private String regimeTributario;

    @Column(name = "situacao_cadastral", length = 30)
    private String situacaoCadastral;

    @Column(name = "ultima_consulta_em")
    private LocalDateTime ultimaConsultaEm;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "criado_por_id")
    private UsuarioJpaEntity criadoPor;
}
