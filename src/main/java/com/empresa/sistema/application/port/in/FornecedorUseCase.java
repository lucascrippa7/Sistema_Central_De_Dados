package com.empresa.sistema.application.port.in;

import com.empresa.sistema.domain.model.Fornecedor;
import com.empresa.sistema.domain.model.UsuarioResumo;

import java.util.List;

public interface FornecedorUseCase {

    List<Fornecedor> listarPorCliente(Long clienteId);

    /**
     * Adiciona um fornecedor pelo CNPJ -- consulta automaticamente razao
     * social e situacao cadastral na hora (ver ConsultarCnpjUseCase). Se a
     * consulta falhar (CNPJ invalido, servico fora do ar), o fornecedor
     * ainda e adicionado, so sem esses dados preenchidos -- da pra tentar
     * de novo depois com "reconsultar".
     */
    Fornecedor adicionar(Long clienteId, String cnpj, UsuarioResumo usuarioLogado);

    /** Consulta de novo a situacao cadastral -- a situacao de um CNPJ pode mudar com o tempo. */
    Fornecedor reconsultarSituacao(Long fornecedorId, UsuarioResumo usuarioLogado);

    void remover(Long fornecedorId, UsuarioResumo usuarioLogado);
}
