package com.empresa.sistema.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Criterios de busca de clientes. Qualquer campo nulo/em branco significa
 * "sem filtro nesse campo" -- para adicionar um novo filtro no futuro
 * (ex: tipo de servico), basta um campo novo aqui e uma Specification nova.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FiltroCliente {
    private String termo;
    private String estado;
    private String regimeTributario;

    /** null = qualquer status; true = so ativos; false = so inativos. */
    private Boolean ativo;

    /** Busca parcial pelo codigo do cliente em outro sistema (ex: Dominio). */
    private String codigoSistemaExterno;

    /** Filtra so os clientes de um responsavel especifico (usado no "Meus clientes"). */
    private Long responsavelId;
}
