package com.empresa.sistema.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/** Resultado de um lote de importacao de XML de NF-e (um ou varios arquivos, ou um .zip) -- Fase 3 da reforma tributaria. */
@Getter
@NoArgsConstructor
public class ResultadoImportacaoNfe {
    private int totalArquivos;
    private int importadosComSucesso;
    private int ignoradosDuplicados;
    private final List<String> erros = new ArrayList<>();

    public void registrarSucesso() {
        totalArquivos++;
        importadosComSucesso++;
    }

    public void registrarDuplicado(String identificacao) {
        totalArquivos++;
        ignoradosDuplicados++;
        erros.add(identificacao + ": nota ja importada anteriormente (mesma chave de acesso), ignorada.");
    }

    public void registrarErro(String nomeArquivo, String motivo) {
        totalArquivos++;
        erros.add(nomeArquivo + ": " + motivo);
    }

    public boolean isTudoOk() {
        return erros.isEmpty();
    }
}
