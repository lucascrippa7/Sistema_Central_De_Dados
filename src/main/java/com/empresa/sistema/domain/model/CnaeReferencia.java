package com.empresa.sistema.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Um CNAE da tabela de referencia (codigo + descricao oficiais) -- usado
 * so pra alimentar a lista de sugestao no cadastro de CNAE de um cliente.
 * Nao tem relacao com ClienteCnae (que e o CNAE de fato vinculado a um
 * cliente) -- esse aqui e so o "catalogo" de onde a pessoa escolhe.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CnaeReferencia {
    private String codigo;
    private String descricao;
}
