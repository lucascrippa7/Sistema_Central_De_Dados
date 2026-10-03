package com.empresa.sistema.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Um item (produto/servico) dentro de uma NotaFiscalEntrada -- Fase 3 do
 * modulo de reforma tributaria. Guarda os dados brutos extraidos do XML
 * (CFOP, CST de ICMS/PIS/COFINS, valores) mais o credito ja calculado pela
 * regra vigente (ver RegrasCreditoTributario).
 *
 * Os valores brutos ficam salvos independente da regra ter mudado depois --
 * se a regra de credito for trocada no futuro (ex: planilha propria do
 * usuario), da pra reprocessar os itens sem precisar reimportar o XML.
 */
@Getter
@Setter
@NoArgsConstructor
public class ItemNotaFiscal {
    private Long id;

    private Integer numeroItem;
    private String descricaoProduto;
    private String ncm;
    private String cfop;

    private String cstIcms;
    private String cstPis;
    private String cstCofins;

    private BigDecimal valorProduto = BigDecimal.ZERO;
    private BigDecimal valorIcms = BigDecimal.ZERO;
    private BigDecimal valorPis = BigDecimal.ZERO;
    private BigDecimal valorCofins = BigDecimal.ZERO;

    /** Parte do valorIcms/valorPis/valorCofins considerada credito aproveitavel pela regra vigente. */
    private BigDecimal creditoIcms = BigDecimal.ZERO;
    private BigDecimal creditoPis = BigDecimal.ZERO;
    private BigDecimal creditoCofins = BigDecimal.ZERO;
}
