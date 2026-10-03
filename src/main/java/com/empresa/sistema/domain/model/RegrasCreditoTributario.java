package com.empresa.sistema.domain.model;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Regra de credito de ICMS/PIS/COFINS aplicada a cada item de NF-e importado
 * -- Fase 3 do modulo de reforma tributaria.
 *
 * IMPORTANTE: esta e uma regra GENERICA baseada na legislacao padrao (mesmo
 * espirito da tabela de referencia de CNAE ja usada no sistema): serve como
 * ponto de partida para ter uma estimativa de credito logo na importacao,
 * mas NAO substitui uma analise tributaria especifica caso a caso (aliquotas
 * por estado, beneficios fiscais, substituicao tributaria, regime do
 * cliente, etc. nao sao considerados aqui).
 *
 * Quando o usuario trouxer a planilha com as regras proprias da empresa,
 * essa classe e o UNICO lugar que precisa mudar -- o parser de XML e a tela
 * continuam os mesmos, porque os valores brutos de cada item ficam salvos
 * (ver ItemNotaFiscal) e podem ser reprocessados com uma regra nova.
 *
 * Regra atual:
 * - ICMS: gera credito se o CFOP for de entrada (comeca com 1, 2 ou 3) e o
 *   CST/CSOSN NAO for isento/nao-tributado/suspenso (40, 41, 50) nem um CFOP
 *   de simples remessa/devolucao (exclusoes abaixo). Credito = valor de ICMS
 *   destacado na nota.
 * - PIS/COFINS: regime nao-cumulativo -- gera credito quando o CST de
 *   entrada esta na faixa "com direito a credito" (50 a 66, conforme
 *   Instrucao Normativa RFB). Credito = valor de PIS/COFINS destacado.
 */
public final class RegrasCreditoTributario {

    private static final Set<String> CST_ICMS_SEM_CREDITO = Set.of("40", "41", "50");

    private static final Set<String> CFOP_SEM_CREDITO = Set.of(
            // devolucoes de venda e simples remessa (nao representam compra)
            "1202", "1203", "1204", "2202", "2203", "2204",
            "1901", "1902", "2901", "2902", "1917", "2917", "1918", "2918"
    );

    private static final Set<String> CST_PIS_COFINS_COM_CREDITO = Set.of(
            "50", "51", "52", "53", "54", "55", "56", "60", "61", "62", "63", "64", "65", "66"
    );

    private RegrasCreditoTributario() {
    }

    public static void aplicar(ItemNotaFiscal item) {
        item.setCreditoIcms(calcularCreditoIcms(item));
        item.setCreditoPis(calcularCreditoPisCofins(item.getCstPis(), item.getValorPis()));
        item.setCreditoCofins(calcularCreditoPisCofins(item.getCstCofins(), item.getValorCofins()));
    }

    private static BigDecimal calcularCreditoIcms(ItemNotaFiscal item) {
        String cfop = item.getCfop();
        String cst = item.getCstIcms();
        BigDecimal valorIcms = item.getValorIcms() == null ? BigDecimal.ZERO : item.getValorIcms();

        if (cfop == null || cfop.isBlank()) {
            return BigDecimal.ZERO;
        }
        boolean entrada = cfop.startsWith("1") || cfop.startsWith("2") || cfop.startsWith("3");
        if (!entrada || CFOP_SEM_CREDITO.contains(cfop)) {
            return BigDecimal.ZERO;
        }
        if (cst != null && CST_ICMS_SEM_CREDITO.contains(cst)) {
            return BigDecimal.ZERO;
        }
        return valorIcms;
    }

    private static BigDecimal calcularCreditoPisCofins(String cst, BigDecimal valor) {
        if (cst == null || valor == null) {
            return BigDecimal.ZERO;
        }
        return CST_PIS_COFINS_COM_CREDITO.contains(cst) ? valor : BigDecimal.ZERO;
    }
}
