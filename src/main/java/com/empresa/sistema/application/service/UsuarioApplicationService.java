package com.empresa.sistema.application.service;

import com.empresa.sistema.application.port.in.AuditoriaUseCase;
import com.empresa.sistema.application.port.in.UsuarioUseCase;
import com.empresa.sistema.application.port.out.CodificadorSenhaPort;
import com.empresa.sistema.application.port.out.UsuarioRepositoryPort;
import com.empresa.sistema.domain.exception.LoginJaExisteException;
import com.empresa.sistema.domain.exception.SenhaAtualIncorretaException;
import com.empresa.sistema.domain.exception.UsuarioNaoEncontradoException;
import com.empresa.sistema.domain.model.AcaoAuditoria;
import com.empresa.sistema.domain.model.Usuario;
import com.empresa.sistema.domain.model.UsuarioResumo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioApplicationService implements UsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final CodificadorSenhaPort codificadorSenhaPort;
    private final AuditoriaUseCase auditoriaUseCase;

    public UsuarioApplicationService(UsuarioRepositoryPort usuarioRepositoryPort,
                                      CodificadorSenhaPort codificadorSenhaPort,
                                      AuditoriaUseCase auditoriaUseCase) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.codificadorSenhaPort = codificadorSenhaPort;
        this.auditoriaUseCase = auditoriaUseCase;
    }

    @Override
    public List<UsuarioResumo> listarAtivos() {
        return usuarioRepositoryPort.findAllAtivosResumo();
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepositoryPort.findAll();
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return usuarioRepositoryPort.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
    }

    @Override
    @Transactional
    public Usuario criar(Usuario usuario, UsuarioResumo usuarioLogado) {
        if (usuarioRepositoryPort.existsByLogin(usuario.getLogin())) {
            throw new LoginJaExisteException(usuario.getLogin());
        }

        usuario.setSenhaHash(codificadorSenhaPort.codificar(usuario.getSenha()));
        usuario.setSenha(null);
        usuario.setAtivo(true);
        usuario.setCriadoEm(LocalDateTime.now());

        Usuario salvo = usuarioRepositoryPort.save(usuario);

        auditoriaUseCase.registrar(usuarioLogado, "USUARIO", salvo.getId(), AcaoAuditoria.CRIACAO,
                "Usuario criado: " + salvo.getLogin());

        return salvo;
    }

    @Override
    @Transactional
    public Usuario atualizar(Long id, Usuario dadosAtualizados, UsuarioResumo usuarioLogado) {
        Usuario existente = buscarPorId(id);

        if (!existente.getLogin().equals(dadosAtualizados.getLogin())
                && usuarioRepositoryPort.existsByLogin(dadosAtualizados.getLogin())) {
            throw new LoginJaExisteException(dadosAtualizados.getLogin());
        }

        existente.setNome(dadosAtualizados.getNome());
        existente.setLogin(dadosAtualizados.getLogin());
        existente.setEmail(dadosAtualizados.getEmail());
        existente.setCargo(dadosAtualizados.getCargo());
        existente.setTipoAcesso(dadosAtualizados.getTipoAcesso());
        existente.setAtivo(dadosAtualizados.isAtivo());

        Usuario salvo = usuarioRepositoryPort.save(existente);

        auditoriaUseCase.registrar(usuarioLogado, "USUARIO", salvo.getId(), AcaoAuditoria.EDICAO,
                "Usuario editado: " + salvo.getLogin());

        return salvo;
    }

    @Override
    @Transactional
    public void redefinirSenha(Long id, String novaSenha, UsuarioResumo usuarioLogado) {
        Usuario existente = buscarPorId(id);
        existente.setSenhaHash(codificadorSenhaPort.codificar(novaSenha));
        usuarioRepositoryPort.save(existente);

        auditoriaUseCase.registrar(usuarioLogado, "USUARIO", id, AcaoAuditoria.EDICAO,
                "Senha redefinida pelo administrador para: " + existente.getLogin());
    }

    @Override
    @Transactional
    public void trocarPropriaSenha(Long usuarioId, String senhaAtual, String novaSenha) {
        Usuario existente = buscarPorId(usuarioId);

        if (!codificadorSenhaPort.confere(senhaAtual, existente.getSenhaHash())) {
            throw new SenhaAtualIncorretaException();
        }

        existente.setSenhaHash(codificadorSenhaPort.codificar(novaSenha));
        usuarioRepositoryPort.save(existente);

        auditoriaUseCase.registrar(existente.paraResumo(), "USUARIO", usuarioId, AcaoAuditoria.EDICAO,
                "Senha alterada pelo proprio usuario");
    }

    @Override
    public void atualizarProprioEmail(Long usuarioId, String novoEmail) {
        Usuario existente = buscarPorId(usuarioId);
        existente.setEmail(novoEmail);
        usuarioRepositoryPort.save(existente);

        auditoriaUseCase.registrar(existente.paraResumo(), "USUARIO", usuarioId, AcaoAuditoria.EDICAO,
                "E-mail atualizado pelo proprio usuario");
    }
}
