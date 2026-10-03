package com.empresa.sistema.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_nota_fiscal_entrada")
@Getter
@Setter
@NoArgsConstructor
public class ItemNotaFiscalJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nota_fiscal_id", nullable = false)
    private NotaFiscalEntradaJpaEntity notaFiscal;

    @Column(name = "numero_item")
    private Integer numeroItem;

    @Column(name = "descricao_produto", length = 500)
    private String descricaoProduto;

    @Column(length = 10)
    private String ncm;

    @Column(length = 10)
    private String cfop;

    @Column(name = "cst_icms", length = 5)
    private String cstIcms;

    @Column(name = "cst_pis", length = 5)
    private String cstPis;

    @Column(name = "cst_cofins", length = 5)
    private String cstCofins;

    @Column(name = "valor_produto", precision = 15, scale = 2)
    private BigDecimal valorProduto = BigDecimal.ZERO;

    @Column(name = "valor_icms", precision = 15, scale = 2)
    private BigDecimal valorIcms = BigDecimal.ZERO;

    @Column(name = "valor_pis", precision = 15, scale = 2)
    private BigDecimal valorPis = BigDecimal.ZERO;

    @Column(name = "valor_cofins", precision = 15, scale = 2)
    private BigDecimal valorCofins = BigDecimal.ZERO;

    @Column(name = "credito_icms", precision = 15, scale = 2)
    private BigDecimal creditoIcms = BigDecimal.ZERO;

    @Column(name = "credito_pis", precision = 15, scale = 2)
    private BigDecimal creditoPis = BigDecimal.ZERO;

    @Column(name = "credito_cofins", precision = 15, scale = 2)
    private BigDecimal creditoCofins = BigDecimal.ZERO;
}
