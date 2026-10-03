package com.empresa.sistema.reforma.application.service;

import com.empresa.sistema.reforma.application.port.in.SimulacaoRegimeUseCase;
import com.empresa.sistema.reforma.domain.model.AnexoSimples;
import com.empresa.sistema.reforma.domain.model.ResultadoSimulacaoRegime;
import com.empresa.sistema.reforma.domain.model.SetorEconomico;
import com.empresa.sistema.reforma.domain.model.SimulacaoRegimeInput;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Implementacao do calculo da simulacao (Fase 1).
 *
 * Os valores usados aqui sao ESTIMATIVAS DE REFERENCIA baseadas nas
 * regras publicadas ate a data da implementacao (EC 132/2023, LC
 * 214/2025 e LC 227/2026) -- a aliquota padrao da CBS/IBS ainda depende
 * de Resolucao do Senado (CBS) e do CGIBS (IBS), entao este calculo
 * deve ser revisado quando essas resolucoes forem publicadas.
 *
 * Nao persiste nada nesta fase -- e so calculo em memoria.
 */
@Service
public class SimulacaoRegimeApplicationService implements SimulacaoRegimeUseCase {

    /** Estimativa tecnica da aliquota padrao combinada de CBS + IBS apos a transicao plena (2033). Fonte: estudos tecnicos publicados em 2026, sujeito a alteracao por resolucao. */
    private static final BigDecimal ALIQUOTA_REFERENCIA_CBS_IBS = new BigDecimal("26.50");

    /** Teto de faturamento anual do Simples Nacional, usado para interpolar a aliquota efetiva estimada. */
    private static final BigDecimal TETO_FATURAMENTO_SIMPLES = new BigDecimal("4800000");

    /** Carga media aproximada no Lucro Presumido para servicos (IRPJ + CSLL + PIS + COFINS + ISS medio). Ponto de partida -- varia por municipio (ISS) e por atividade. */
    private static final BigDecimal CARGA_APROXIMADA_PRESUMIDO = new BigDecimal("14.83");

    /** Carga aproximada no Lucro Real -- varia muito conforme margem/creditos da empresa; usada so como piso de referencia na Fase 1. */
    private static final BigDecimal CARGA_APROXIMADA_REAL = new BigDecimal("18.00");

    @Override
    public ResultadoSimulacaoRegime simular(SimulacaoRegimeInput input) {
        BigDecimal cargaAtual = calcularCargaAtual(input);
        BigDecimal cargaProjetada = calcularCargaProjetada(input);

        ResultadoSimulacaoRegime resultado = new ResultadoSimulacaoRegime();
        resultado.setCargaTributariaAtual(arredondar(cargaAtual));
        resultado.setCargaTributariaProjetada(arredondar(cargaProjetada));
        resultado.setDiferencaPercentual(arredondar(calcularDiferencaPercentual(cargaAtual, cargaProjetada)));
        resultado.setRecomendacao(gerarRecomendacao(input, cargaAtual, cargaProjetada));
        return resultado;
    }

    private BigDecimal calcularCargaAtual(SimulacaoRegimeInput input) {
        return switch (input.getRegimeAtual()) {
            case SIMPLES_NACIONAL -> calcularCargaSimples(input);
            case LUCRO_PRESUMIDO -> CARGA_APROXIMADA_PRESUMIDO;
            case LUCRO_REAL -> CARGA_APROXIMADA_REAL;
        };
    }

    /**
     * Interpolacao linear simples entre a aliquota inicial e a aliquota da
     * ultima faixa do anexo, proporcional ao quanto o faturamento anual
     * informado ocupa do teto de R$ 4,8mi. Nao e o calculo oficial (que usa
     * a formula "aliquota nominal - parcela a deduzir, dividido pela RBT12"),
     * mas da uma estimativa direcional suficiente pra Fase 1.
     */
    private BigDecimal calcularCargaSimples(SimulacaoRegimeInput input) {
        AnexoSimples anexo = mapearSetorParaAnexo(input.getSetor());

        BigDecimal proporcaoFaturamento = input.getFaturamentoAnual()
                .divide(TETO_FATURAMENTO_SIMPLES, 4, RoundingMode.HALF_UP)
                .min(BigDecimal.ONE);

        BigDecimal variacao = BigDecimal.valueOf(anexo.getAliquotaFaixaMaxima() - anexo.getAliquotaInicial());

        return BigDecimal.valueOf(anexo.getAliquotaInicial())
                .add(variacao.multiply(proporcaoFaturamento));
    }

    private AnexoSimples mapearSetorParaAnexo(SetorEconomico setor) {
        return switch (setor) {
            case COMERCIO -> AnexoSimples.ANEXO_I;
            case INDUSTRIA -> AnexoSimples.ANEXO_II;
            case SERVICOS -> AnexoSimples.ANEXO_III;
        };
    }

    /**
     * A reforma preve credito amplo na cadeia: quem vende para outras
     * empresas (B2B) tende a repassar boa parte da carga adiante (o
     * comprador credita o que pagou), enquanto vendas para consumidor
     * final absorvem a carga cheia, sem repasse via credito.
     *
     * Aqui isso e representado de forma simplificada: a fatia B2B do
     * faturamento pesa menos na carga efetiva projetada do que a fatia
     * vendida a consumidor final. O fator 0.3 e uma estimativa de quanto
     * da carga ainda "sobra" mesmo em vendas B2B (ex: parcela nao
     * credita, custos administrativos da transicao) -- nao vem de uma
     * fonte oficial, e um ponto de partida para calibrar depois com
     * casos reais.
     */
    private BigDecimal calcularCargaProjetada(SimulacaoRegimeInput input) {
        BigDecimal percentualB2B = input.getPercentualVendaB2B().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal percentualConsumidorFinal = BigDecimal.ONE.subtract(percentualB2B);

        BigDecimal fatorResidualB2B = new BigDecimal("0.3");

        BigDecimal parteConsumidorFinal = ALIQUOTA_REFERENCIA_CBS_IBS.multiply(percentualConsumidorFinal);
        BigDecimal parteB2B = ALIQUOTA_REFERENCIA_CBS_IBS.multiply(percentualB2B).multiply(fatorResidualB2B);

        return parteConsumidorFinal.add(parteB2B);
    }

    private BigDecimal calcularDiferencaPercentual(BigDecimal cargaAtual, BigDecimal cargaProjetada) {
        if (cargaAtual.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return cargaProjetada.subtract(cargaAtual)
                .divide(cargaAtual, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private String gerarRecomendacao(SimulacaoRegimeInput input, BigDecimal cargaAtual, BigDecimal cargaProjetada) {
        BigDecimal diferenca = cargaProjetada.subtract(cargaAtual);

        if (diferenca.compareTo(BigDecimal.ZERO) <= 0) {
            return "A carga projetada tende a ficar igual ou menor que a atual. O regime atual parece competitivo, "
                    + "mas vale confirmar com o contador antes de qualquer decisao -- esta e uma estimativa de referencia.";
        }
        if (input.getPercentualVendaB2B().compareTo(new BigDecimal("60")) >= 0) {
            return "A carga projetada e maior, porem com boa parcela de vendas para empresas (B2B) -- nesse perfil, "
                    + "migrar para o regime regular pode compensar pelo credito cheio repassado aos clientes. "
                    + "Avaliar com mais detalhe junto ao contador.";
        }
        return "A carga projetada e maior e a parcela de vendas B2B e baixa -- nesse perfil, a migracao tende a "
                + "nao compensar, ja que a maior parte das vendas nao gera credito repassavel. Recomenda-se "
                + "avaliar o cenario completo com o contador antes de 2027.";
    }

    private BigDecimal arredondar(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }
}
