package com.empresa.sistema.application.port.in;

import com.empresa.sistema.domain.model.AnexoCliente;
import com.empresa.sistema.domain.model.UsuarioResumo;

import java.util.List;

public interface AnexoClienteUseCase {

    /** Lista os anexos de um cliente -- SEM o conteudo (so pra mostrar nome/tamanho/data na tela). */
    List<AnexoCliente> listarPorCliente(Long clienteId);

    /** Com o conteudo -- usado so na hora de baixar o arquivo de verdade. */
    AnexoCliente baixar(Long anexoId);

    AnexoCliente adicionar(Long clienteId, String nomeArquivo, String tipoConteudo, byte[] conteudo,
                            UsuarioResumo usuarioLogado);

    void remover(Long anexoId, UsuarioResumo usuarioLogado);
}
