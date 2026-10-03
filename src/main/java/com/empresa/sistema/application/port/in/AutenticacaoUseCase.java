package com.empresa.sistema.application.port.in;

import com.empresa.sistema.domain.exception.UsuarioNaoEncontradoException;
import com.empresa.sistema.domain.model.Usuario;

/**
 * Caso de uso de autenticacao: carregar um usuario pelo login.
 * Quem chama isso e o adaptador do Spring Security (infrastructure/security),
 * traduzindo para o formato que o Spring entende.
 */
public interface AutenticacaoUseCase {
    Usuario carregarPorLogin(String login) throws UsuarioNaoEncontradoException;
}
