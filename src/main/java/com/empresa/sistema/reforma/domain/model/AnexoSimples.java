package com.empresa.sistema.reforma.domain.model;

/**
 * Aliquotas de referencia (inicial e da ultima faixa) por anexo do
 * Simples Nacional, usadas so como PONTO DE PARTIDA para a Fase 1
 * do simulador.
 *
 * IMPORTANTE: o Simples usa aliquota efetiva progressiva calculada
 * por faixa de RBT12 (receita bruta dos ultimos 12 meses), nao uma
 * aliquota fixa. Aqui interpolamos linearmente entre a faixa inicial
 * e a ultima faixa (R$ 4,8mi/ano) so para dar uma estimativa
 * direcional -- nao substitui o calculo oficial da tabela do Simples.
 * Se algum dia precisar de precisao real, isso deve ser trocado pela
 * tabela completa (5 faixas, cada uma com aliquota nominal + parcela
 * a deduzir).
 */
public enum AnexoSimples {

    /** Comercio em geral. */
    ANEXO_I(4.0, 19.0),

    /** Industria em geral. */
    ANEXO_II(4.5, 30.5),

    /** Servicos em geral (contabilidade, TI, consultoria, etc.). */
    ANEXO_III(6.0, 33.0),

    /** Servicos com fator R baixo (sem reducao de CPP na aliquota). */
    ANEXO_IV(4.5, 33.0),

    /** Servicos com alta folha de pagamento (fator R >= 28%). */
    ANEXO_V(15.5, 30.5);

    private final double aliquotaInicial;
    private final double aliquotaFaixaMaxima;

    AnexoSimples(double aliquotaInicial, double aliquotaFaixaMaxima) {
        this.aliquotaInicial = aliquotaInicial;
        this.aliquotaFaixaMaxima = aliquotaFaixaMaxima;
    }

    public double getAliquotaInicial() {
        return aliquotaInicial;
    }

    public double getAliquotaFaixaMaxima() {
        return aliquotaFaixaMaxima;
    }
}
