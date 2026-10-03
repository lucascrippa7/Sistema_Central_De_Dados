package com.empresa.sistema.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Resumo do que aconteceu ao importar uma planilha de clientes: quantas
 * linhas vieram, quantas viraram cliente de verdade, e o que deu errado
 * linha a linha (pra mostrar pro usuario o que precisa corrigir).
 */
@Getter
@Setter
public class ResultadoImportacao {
    private int totalLinhas;
    private int importados;
    private List<String> erros = new ArrayList<>();

    public void adicionarErro(String mensagem) {
        erros.add(mensagem);
    }

    public void incrementarImportados() {
        importados++;
    }
}
