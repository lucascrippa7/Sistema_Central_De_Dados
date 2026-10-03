package com.empresa.sistema.application.port.in;

import com.empresa.sistema.domain.model.Credencial;
import com.empresa.sistema.domain.model.UsuarioResumo;

import java.util.List;

public interface CredencialUseCase {

    /** Sempre retorna as credenciais com senha=null -- nunca expoe a senha aqui. */
    List<Credencial> listarPorCliente(Long clienteId);

    Credencial adicionar(Long clienteId, Credencial credencial, UsuarioResumo usuarioLogado);

    void remover(Long credencialId, UsuarioResumo usuarioLogado);

    /** Unico ponto que devolve a senha em texto puro -- e registra auditoria ao fazer isso. */
    String revelarSenha(Long credencialId, UsuarioResumo usuarioLogado);
}
