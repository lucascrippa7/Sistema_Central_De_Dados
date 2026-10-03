package com.empresa.sistema.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Uma NF-e de saida (venda) importada via XML para um cliente -- Fase 4 do
 * modulo de reforma tributaria (faturamento historico). So guarda o
 * cabecalho e o valor total -- ainda nao guarda itens, porque por enquanto
 * so alimenta o faturamento anual (ver HistoricoFaturamentoUseCase). Se no
 * futuro entrar a analise por servico/produto (reducao de aliquota da LC
 * 214/2025), itens podem ser adicionados do mesmo jeito que em
 * NotaFiscalEntrada.
 */
@Getter
@Setter
@NoArgsConstructor
public class NotaFiscalSaida {
    private Long id;
    private Long clienteId;

    private String chaveAcesso;
    private String numero;
    private String serie;
    private LocalDate dataEmissao;

    private String destinatarioCnpj;
    private String destinatarioRazaoSocial;

    private BigDecimal valorTotalNota = BigDecimal.ZERO;

    private String nomeArquivoOriginal;
    private LocalDateTime importadoEm;
    private UsuarioResumo importadoPor;
}
