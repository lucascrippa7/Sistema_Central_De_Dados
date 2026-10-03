package com.empresa.sistema.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Uma NF-e de entrada (compra) importada via XML para um cliente -- Fase 3
 * do modulo de reforma tributaria. Guarda o cabecalho da nota e os itens
 * (ver ItemNotaFiscal), com o credito de ICMS/PIS/COFINS ja calculado pela
 * regra vigente no momento da importacao.
 */
@Getter
@Setter
@NoArgsConstructor
public class NotaFiscalEntrada {
    private Long id;
    private Long clienteId;

    /** Chave de acesso de 44 digitos -- usada para nao importar a mesma nota duas vezes. */
    private String chaveAcesso;
    private String numero;
    private String serie;
    private LocalDate dataEmissao;

    private String fornecedorCnpj;
    private String fornecedorRazaoSocial;

    private BigDecimal valorTotalNota = BigDecimal.ZERO;

    private String nomeArquivoOriginal;
    private LocalDateTime importadoEm;
    private UsuarioResumo importadoPor;

    private List<ItemNotaFiscal> itens = new ArrayList<>();

    public BigDecimal getTotalCreditoIcms() {
        return itens.stream().map(ItemNotaFiscal::getCreditoIcms).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalCreditoPis() {
        return itens.stream().map(ItemNotaFiscal::getCreditoPis).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalCreditoCofins() {
        return itens.stream().map(ItemNotaFiscal::getCreditoCofins).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalCreditoGeral() {
        return getTotalCreditoIcms().add(getTotalCreditoPis()).add(getTotalCreditoCofins());
    }
}
