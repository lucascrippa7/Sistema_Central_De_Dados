package com.empresa.sistema.application.port.out;

import lombok.Getter;
import lombok.Setter;

/**
 * Representa uma linha crua lida de uma planilha de importacao de clientes
 * -- ainda nao e um Cliente de dominio, so os valores como vieram das
 * celulas. A aplicacao (ClienteExcelApplicationService) e quem decide como
 * transformar isso num Cliente de verdade (e o que fazer se algo estiver
 * invalido).
 */
@Getter
@Setter
public class LinhaClientePlanilha {
    private int numeroLinha;
    private String razaoSocial;
    private String nomeFantasia;
    private String cnpj;
    private String regimeTributario;
    private String cidade;
    private String estado;
    private String tipoServico;
    private String descricaoServico;
    private String inscricaoMunicipal;
    private String inscricaoEstadual;
    private String codigoSistemaExterno;
    private String responsavelLogin;
    private boolean ativo = true;
}
