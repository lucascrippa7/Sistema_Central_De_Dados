package com.empresa.sistema.application.port.out;

import com.empresa.sistema.domain.model.Cliente;
import com.empresa.sistema.domain.model.ColunaClientePlanilha;

import java.util.List;
import java.util.Set;

/**
 * Porta de saida para gerar uma planilha .xlsx a partir de uma lista de
 * clientes. A aplicacao decide QUAIS clientes e QUAIS colunas entram;
 * como o arquivo e montado de verdade (Apache POI) fica isolado na
 * infraestrutura. Razao Social sempre entra, independente das colunas
 * escolhidas -- sem ela a planilha nao serve pra nada.
 */
public interface GeradorPlanilhaClientesPort {
    byte[] gerar(List<Cliente> clientes, Set<ColunaClientePlanilha> colunas);
}
