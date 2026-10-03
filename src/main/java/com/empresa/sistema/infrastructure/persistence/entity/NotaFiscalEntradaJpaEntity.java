package com.empresa.sistema.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notas_fiscais_entrada")
@Getter
@Setter
@NoArgsConstructor
public class NotaFiscalEntradaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteJpaEntity cliente;

    @Column(name = "chave_acesso", nullable = false, length = 44)
    private String chaveAcesso;

    @Column(length = 20)
    private String numero;

    @Column(length = 10)
    private String serie;

    @Column(name = "data_emissao")
    private LocalDate dataEmissao;

    @Column(name = "fornecedor_cnpj", length = 18)
    private String fornecedorCnpj;

    @Column(name = "fornecedor_razao_social", length = 200)
    private String fornecedorRazaoSocial;

    @Column(name = "valor_total_nota", precision = 15, scale = 2)
    private BigDecimal valorTotalNota = BigDecimal.ZERO;

    @Column(name = "nome_arquivo_original", length = 255)
    private String nomeArquivoOriginal;

    @Column(name = "importado_em", nullable = false, updatable = false)
    private LocalDateTime importadoEm = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "importado_por_id")
    private UsuarioJpaEntity importadoPor;

    @OneToMany(mappedBy = "notaFiscal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemNotaFiscalJpaEntity> itens = new ArrayList<>();
}
