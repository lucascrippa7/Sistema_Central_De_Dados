package com.empresa.sistema.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
public class ClienteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "razao_social", nullable = false, length = 200)
    private String razaoSocial;

    @Column(name = "nome_fantasia", length = 200)
    private String nomeFantasia;

    @Column(length = 18, unique = true)
    private String cnpj;

    @Column(name = "regime_tributario", length = 60)
    private String regimeTributario;

    @Column(length = 100)
    private String cidade;

    @Column(length = 2)
    private String estado;

    @Column(name = "tipo_servico", length = 150)
    private String tipoServico;

    @Column(name = "descricao_servico", columnDefinition = "TEXT")
    private String descricaoServico;

    @Column(name = "categoria_reducao_lc214", length = 30)
    private String categoriaReducaoLc214;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "inscricao_municipal", length = 30)
    private String inscricaoMunicipal;

    @Column(name = "inscricao_estadual", length = 30)
    private String inscricaoEstadual;

    @Column(name = "codigo_sistema_externo", length = 50)
    private String codigoSistemaExterno;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private UsuarioJpaEntity responsavel;

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

    // LAZY simples (sem @Fetch customizado) -- ja tentamos SUBSELECT aqui pra
    // evitar N+1 na tela de planilha, mas isso quebrou adicionar CNAE (o
    // formulario nao dava erro, so nao persistia o item novo). Prioridade e
    // a funcionalidade principal funcionar direito; a otimizacao de leitura
    // fica pra depois, com mais tempo pra testar com calma.
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClienteCnaeJpaEntity> cnaes = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClienteCodigoServicoJpaEntity> codigosServico = new ArrayList<>();
}
