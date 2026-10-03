package com.empresa.sistema.domain.model;

/**
 * Colunas OPCIONAIS que podem entrar numa planilha de clientes exportada.
 * "Razao Social" nao esta na lista porque e sempre incluida -- sem ela a
 * planilha nao serve pra nada.
 */
public enum ColunaClientePlanilha {
    CNPJ("CNPJ"),
    NOME_FANTASIA("Nome Fantasia"),
    REGIME_TRIBUTARIO("Regime Tributario"),
    CIDADE("Cidade"),
    ESTADO("UF"),
    TIPO_SERVICO("Tipo de Servico"),
    DESCRICAO_SERVICO("Descricao do Servico"),
    INSCRICAO_MUNICIPAL("Inscricao Municipal"),
    INSCRICAO_ESTADUAL("Inscricao Estadual"),
    CODIGO_SISTEMA_EXTERNO("Codigo em Outro Sistema"),
    RESPONSAVEL("Responsavel (login)"),
    STATUS("Ativo"),
    CNAES("CNAEs"),
    CODIGOS_SERVICO("Codigos de Servico");

    private final String rotulo;

    ColunaClientePlanilha(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
