package com.empresa.sistema.application.port.in;

import com.empresa.sistema.domain.model.ColunaClientePlanilha;
import com.empresa.sistema.domain.model.FiltroCliente;
import com.empresa.sistema.domain.model.ResultadoImportacao;
import com.empresa.sistema.domain.model.UsuarioResumo;

import java.io.InputStream;
import java.util.Set;

public interface ClienteExcelUseCase {

    /**
     * Gera a planilha dos clientes que batem com o filtro. "colunas" e o
     * que o usuario escolheu ver alem da Razao Social (que e sempre
     * incluida); se vier nulo ou vazio, todas as colunas entram.
     */
    byte[] exportar(FiltroCliente filtro, Set<ColunaClientePlanilha> colunas);

    /** Gera uma planilha vazia, so com o cabecalho dos campos importaveis -- serve de modelo. */
    byte[] gerarModelo();

    ResultadoImportacao importar(InputStream arquivo, UsuarioResumo usuarioLogado);
}
