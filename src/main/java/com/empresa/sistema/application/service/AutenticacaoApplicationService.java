package com.empresa.sistema.application.service;

import com.empresa.sistema.application.port.in.AutenticacaoUseCase;
import com.empresa.sistema.application.port.out.UsuarioRepositoryPort;
import com.empresa.sistema.domain.exception.UsuarioNaoEncontradoException;
import com.empresa.sistema.domain.model.Usuario;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoApplicationService implements AutenticacaoUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public AutenticacaoApplicationService(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    public Usuario carregarPorLogin(String login) {
        return usuarioRepositoryPort.findByLogin(login)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(login));
    }
}
