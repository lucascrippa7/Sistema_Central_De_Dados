package com.empresa.sistema.reforma.application.service;

import com.empresa.sistema.application.port.in.ClienteUseCase;
import com.empresa.sistema.application.port.in.HistoricoFaturamentoUseCase;
import com.empresa.sistema.application.port.in.NotaFiscalEntradaUseCase;
import com.empresa.sistema.application.port.in.ReducaoAliquotaUseCase;
import com.empresa.sistema.domain.model.Cliente;
import com.empresa.sistema.domain.model.HistoricoFaturamentoAno;
import com.empresa.sistema.domain.model.ReducaoAliquotaLc214;
import com.empresa.sistema.domain.model.ResumoCreditoTributario;
import com.empresa.sistema.reforma.application.port.in.ComparativoRegimeUseCase;
import com.empresa.sistema.reforma.domain.model.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Motor de comparacao entre Simples Nacional, Lucro Presumido, Lucro Real e
 * Hibrido (reforma IBS/CBS) -- Fase 4 do modulo de reforma tributaria.
 *
 * IMPORTANTE -- isto e uma ESTIMATIVA DE REFERENCIA, nao um calculo fiscal
 * definitivo:
 * - Simples Nacional: usa a mesma interpolacao linear simplificada da
 *   Fase 1 (SimulacaoRegimeApplicationService) entre a aliquota inicial e a
 *   da ultima faixa do anexo -- nao e a formula oficial (aliquota nominal -
 *   parcela a deduzir, sobre RBT12 por faixa).
 * - Lucro Presumido/Real: usa as presuncoes e aliquotas padrao da lei geral
 *   (Lei 9.249/95) -- IRPJ 15% + adicional 10% acima de R$20mil/mes, CSLL 9%,
 *   PIS/COFINS conforme o regime. ICMS NAO e calculado (varia por estado e
 *   por produto) -- so ISS (quando servico), com aliquota informada no form.
 * - Lucro Real usa uma margem de lucro ESTIMADA informada no formulario,
 *   porque o sistema nao acompanha despesas/lucro contabil real do cliente.
 * - Hibrido: soma a tributacao de consumo pos-reforma (IBS/CBS sobre o
 *   faturamento, com a reducao setorial da LC 214/2025 e os creditos de
 *   compra ja calculados na Fase 3) com o IRPJ/CSLL do Lucro Presumido
 *   (a reforma so mexe nos tributos de consumo -- IRPJ/CSLL continuam
 *   existindo do jeito que sao hoje).
 *
 * Sempre revisar com o contador antes de qualquer decisao -- isso NAO
 * substitui uma analise tributaria formal.
 */
@Service
public class ComparativoRegimeApplicationService implements ComparativoRegimeUseCase {

    private static final BigDecimal ALIQUOTA_CBS_IBS_PADRAO = new BigDecimal("26.50");
    private static final BigDecimal LIMITE_MENSAL_ADICIONAL_IRPJ = new BigDecimal("20000");
    private static final BigDecimal ALIQUOTA_IRPJ = new BigDecimal("15.00");
    private static final BigDecimal ALIQUOTA_ADICIONAL_IRPJ = new BigDecimal("10.00");
    private static final BigDecimal ALIQUOTA_CSLL = new BigDecimal("9.00");
    private static final BigDecimal PIS_CUMULATIVO = new BigDecimal("0.65");
    private static final BigDecimal COFINS_CUMULATIVO = new BigDecimal("3.00");
    private static final BigDecimal PIS_NAO_CUMULATIVO = new BigDecimal("1.65");
    private static final BigDecimal COFINS_NAO_CUMULATIVO = new BigDecimal("7.60");

    private final ClienteUseCase clienteUseCase;
    private final HistoricoFaturamentoUseCase historicoFaturamentoUseCase;
    private final NotaFiscalEntradaUseCase notaFiscalEntradaUseCase;
    private final ReducaoAliquotaUseCase reducaoAliquotaUseCase;

    public ComparativoRegimeApplicationService(ClienteUseCase clienteUseCase,
                                                HistoricoFaturamentoUseCase historicoFaturamentoUseCase,
                                                NotaFiscalEntradaUseCase notaFiscalEntradaUseCase,
                                                ReducaoAliquotaUseCase reducaoAliquotaUseCase) {
        this.clienteUseCase = clienteUseCase;
        this.historicoFaturamentoUseCase = historicoFaturamentoUseCase;
        this.notaFiscalEntradaUseCase = notaFiscalEntradaUseCase;
        this.reducaoAliquotaUseCase = reducaoAliquotaUseCase;
    }

    @Override
    public ResultadoComparativoRegime calcular(Long clienteId, int ano, ComparativoRegimeInput input) {
        Cliente cliente = clienteUseCase.buscarPorId(clienteId);

        BigDecimal faturamento = buscarFaturamentoDoAno(clienteId, ano);
        ResumoCreditoTributario creditos = notaFiscalEntradaUseCase.calcularResumo(
                clienteId, LocalDate.of(ano, 1, 1), LocalDate.of(ano, 12, 31));

        ReducaoAliquotaLc214 reducao = buscarReducao(cliente.getCategoriaReducaoLc214());

        ResultadoComparativoRegime resultado = new ResultadoComparativoRegime();
        resultado.setAno(ano);
        resultado.setFaturamentoUsado(faturamento);
        resultado.setCreditoIcmsUsado(creditos.getTotalCreditoIcms());
        resultado.setCreditoPisUsado(creditos.getTotalCreditoPis());
        resultado.setCreditoCofinsUsado(creditos.getTotalCreditoCofins());
        resultado.setCategoriaReducaoUsada(reducao.getCategoria());

        RegimeCalculado simples = calcularSimples(input.getSetor(), faturamento);
        RegimeCalculado presumido = calcularPresumido(input, faturamento);
        RegimeCalculado real = calcularReal(input, faturamento, creditos);
        RegimeCalculado hibrido = calcularHibrido(input, faturamento, creditos, reducao);

        resultado.setRegimes(List.of(simples, presumido, real, hibrido));
        resultado.setMelhorRegimeNome(resultado.getRegimes().stream()
                .min((a, b) -> a.getCargaTributariaEstimada().compareTo(b.getCargaTributariaEstimada()))
                .map(RegimeCalculado::getNome)
                .orElse("-"));

        resultado.setAviso("Estimativa de referência para apoiar a análise -- não substitui o cálculo formal do contador. "
                + "ICMS não é considerado no Presumido/Real (varia por estado/produto). Lucro Real usa uma margem de lucro "
                + "estimada, não a apuração contábil real do cliente.");
        return resultado;
    }

    private BigDecimal buscarFaturamentoDoAno(Long clienteId, int ano) {
        return historicoFaturamentoUseCase.listarPorCliente(clienteId).stream()
                .filter(item -> item.getAno() == ano)
                .findFirst()
                .map(HistoricoFaturamentoAno::getValorConsiderado)
                .orElse(BigDecimal.ZERO);
    }

    private ReducaoAliquotaLc214 buscarReducao(String codigo) {
        Optional<ReducaoAliquotaLc214> encontrada = reducaoAliquotaUseCase.listar().stream()
                .filter(r -> r.getCodigo().equals(codigo))
                .findFirst();
        return encontrada.orElseGet(() -> new ReducaoAliquotaLc214("SEM_REDUCAO", "Sem redução (alíquota cheia)", BigDecimal.ZERO, "-", "-"));
    }

    /** Mesma interpolacao simplificada da Fase 1 (ver AnexoSimples) -- nao e a formula oficial por faixa/RBT12. */
    private RegimeCalculado calcularSimples(SetorEconomico setor, BigDecimal faturamento) {
        AnexoSimples anexo = switch (setor) {
            case COMERCIO -> AnexoSimples.ANEXO_I;
            case INDUSTRIA -> AnexoSimples.ANEXO_II;
            case SERVICOS -> AnexoSimples.ANEXO_III;
        };
        BigDecimal tetoSimples = new BigDecimal("4800000");
        BigDecimal proporcao = faturamento.divide(tetoSimples, 4, RoundingMode.HALF_UP).min(BigDecimal.ONE);
        BigDecimal variacao = BigDecimal.valueOf(anexo.getAliquotaFaixaMaxima() - anexo.getAliquotaInicial());
        BigDecimal aliquotaEfetiva = BigDecimal.valueOf(anexo.getAliquotaInicial()).add(variacao.multiply(proporcao));

        BigDecimal carga = faturamento.multiply(aliquotaEfetiva).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return new RegimeCalculado("Simples Nacional", carga, arredondar(aliquotaEfetiva),
                "Estimativa por interpolação linear no " + anexo.name().replace("_", " ") + " (não é a tabela oficial por faixa).");
    }

    private RegimeCalculado calcularPresumido(ComparativoRegimeInput input, BigDecimal faturamento) {
        boolean servico = input.getSetor() == SetorEconomico.SERVICOS;

        BigDecimal[] irpjCsll = calcularIrpjCsllPresumido(input, faturamento);
        BigDecimal irpj = irpjCsll[0];
        BigDecimal csll = irpjCsll[1];
        BigDecimal pis = faturamento.multiply(PIS_CUMULATIVO).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal cofins = faturamento.multiply(COFINS_CUMULATIVO).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal iss = servico ? faturamento.multiply(input.getAliquotaIss()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        BigDecimal total = irpj.add(csll).add(pis).add(cofins).add(iss);
        String detalhe = "IRPJ " + irpj + " + CSLL " + csll + " + PIS " + pis + " + COFINS " + cofins
                + (servico ? " + ISS " + iss : " (ICMS não incluído)");
        return new RegimeCalculado("Lucro Presumido", total, percentual(total, faturamento), detalhe);
    }

    /** IRPJ e CSLL pelas regras do Lucro Presumido (Lei 9.249/95) -- extraido a parte pro Hibrido reaproveitar sem duplicar a formula. Retorna [irpj, csll]. */
    private BigDecimal[] calcularIrpjCsllPresumido(ComparativoRegimeInput input, BigDecimal faturamento) {
        boolean servico = input.getSetor() == SetorEconomico.SERVICOS;
        BigDecimal presuncaoIrpj = servico ? new BigDecimal("32") : new BigDecimal("8");
        BigDecimal presuncaoCsll = servico ? new BigDecimal("32") : new BigDecimal("12");

        BigDecimal baseIrpj = faturamento.multiply(presuncaoIrpj).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal baseCsll = faturamento.multiply(presuncaoCsll).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal irpj = calcularIrpjComAdicional(baseIrpj);
        BigDecimal csll = baseCsll.multiply(ALIQUOTA_CSLL).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return new BigDecimal[]{irpj, csll};
    }

    private RegimeCalculado calcularReal(ComparativoRegimeInput input, BigDecimal faturamento, ResumoCreditoTributario creditos) {
        boolean servico = input.getSetor() == SetorEconomico.SERVICOS;
        BigDecimal lucroReal = faturamento.multiply(input.getMargemLucroEstimada()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal irpj = calcularIrpjComAdicional(lucroReal);
        BigDecimal csll = lucroReal.multiply(ALIQUOTA_CSLL).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal pisBruto = faturamento.multiply(PIS_NAO_CUMULATIVO).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal cofinsBruto = faturamento.multiply(COFINS_NAO_CUMULATIVO).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal pis = pisBruto.subtract(creditos.getTotalCreditoPis()).max(BigDecimal.ZERO);
        BigDecimal cofins = cofinsBruto.subtract(creditos.getTotalCreditoCofins()).max(BigDecimal.ZERO);
        BigDecimal iss = servico ? faturamento.multiply(input.getAliquotaIss()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        BigDecimal total = irpj.add(csll).add(pis).add(cofins).add(iss);
        String detalhe = "IRPJ " + irpj + " + CSLL " + csll + " + PIS " + pis + " + COFINS " + cofins
                + " (já descontado o crédito de compras) " + (servico ? "+ ISS " + iss : "(ICMS não incluído)")
                + " -- lucro estimado em " + input.getMargemLucroEstimada() + "% do faturamento.";
        return new RegimeCalculado("Lucro Real", total, percentual(total, faturamento), detalhe);
    }

    private RegimeCalculado calcularHibrido(ComparativoRegimeInput input, BigDecimal faturamento, ResumoCreditoTributario creditos,
                                             ReducaoAliquotaLc214 reducao) {
        BigDecimal fatorReducao = BigDecimal.ONE.subtract(
                reducao.getPercentualReducao().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        BigDecimal aliquotaEfetiva = ALIQUOTA_CBS_IBS_PADRAO.multiply(fatorReducao);

        BigDecimal consumoBruto = faturamento.multiply(aliquotaEfetiva).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal creditoTotal = creditos.getTotalCreditoIcms().add(creditos.getTotalCreditoPis()).add(creditos.getTotalCreditoCofins());
        BigDecimal consumoLiquido = consumoBruto.subtract(creditoTotal).max(BigDecimal.ZERO);

        // IRPJ/CSLL nao mudam com a reforma de consumo (ela so mexe em ICMS/ISS/PIS/COFINS/IPI) --
        // reaproveita a mesma formula do Presumido pra essa parte, sem duplicar o calculo.
        BigDecimal[] irpjCsll = calcularIrpjCsllPresumido(input, faturamento);
        BigDecimal irpjCsllTotal = irpjCsll[0].add(irpjCsll[1]);

        BigDecimal total = consumoLiquido.add(irpjCsllTotal);
        String detalhe = "IBS/CBS: " + consumoLiquido + " (alíquota efetiva " + arredondar(aliquotaEfetiva) + "%, categoria \""
                + reducao.getCategoria() + "\", já descontado o crédito de compras) + IRPJ " + irpjCsll[0] + " + CSLL " + irpjCsll[1]
                + " (regras atuais do Lucro Presumido, que continuam valendo)";
        return new RegimeCalculado("Híbrido (reforma, transição plena)", total, percentual(total, faturamento), detalhe);
    }

    private BigDecimal calcularIrpjComAdicional(BigDecimal base) {
        BigDecimal irpjNormal = base.multiply(ALIQUOTA_IRPJ).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal limiteAnual = LIMITE_MENSAL_ADICIONAL_IRPJ.multiply(BigDecimal.valueOf(12));
        BigDecimal excedente = base.subtract(limiteAnual).max(BigDecimal.ZERO);
        BigDecimal adicional = excedente.multiply(ALIQUOTA_ADICIONAL_IRPJ).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return irpjNormal.add(adicional);
    }

    private BigDecimal percentual(BigDecimal valor, BigDecimal faturamento) {
        if (faturamento.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return valor.divide(faturamento, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal arredondar(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }
}
