package com.empresa.sistema.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Uma "pagina" de clientes -- usado so na tela de lista (que tem paginacao
 * de verdade, pra nao pesar quando a base crescer). As telas de planilha e
 * exportacao continuam usando ClienteUseCase.listar(...) sem paginacao de
 * proposito -- o objetivo delas e mostrar tudo de uma vez.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaginaCliente {
    private List<Cliente> conteudo;
    private int paginaAtual;
    private int totalPaginas;
    private long totalElementos;

    public boolean isTemAnterior() {
        return paginaAtual > 0;
    }

    public boolean isTemProxima() {
        return paginaAtual < totalPaginas - 1;
    }
}
