package com.empresa.sistema.application.port.out;

import com.empresa.sistema.domain.model.DadosCnpj;

import java.util.Optional;

/**
 * Porta de saida pra consulta de CNPJ num servico externo. Vazio (nao
 * lanca excecao) quando o CNPJ nao existe, e invalido, ou o servico externo
 * falhou/esta fora do ar -- a consulta e so uma ajuda de preenchimento,
 * nunca pode travar o cadastro do cliente.
 */
public interface ConsultaCnpjPort {
    Optional<DadosCnpj> consultar(String cnpjSomenteNumeros);
}
