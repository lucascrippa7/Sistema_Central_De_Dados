package com.empresa.sistema.application.service;

import com.empresa.sistema.application.port.in.AuditoriaUseCase;
import com.empresa.sistema.application.port.in.CredencialUseCase;
import com.empresa.sistema.application.port.out.CredencialRepositoryPort;
import com.empresa.sistema.domain.exception.CredencialNaoEncontradaException;
import com.empresa.sistema.domain.model.AcaoAuditoria;
import com.empresa.sistema.domain.model.Credencial;
import com.empresa.sistema.domain.model.UsuarioResumo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CredencialApplicationService implements CredencialUseCase {

    private final CredencialRepositoryPort credencialRepositoryPort;
    private final AuditoriaUseCase auditoriaUseCase;

    public CredencialApplicationService(CredencialRepositoryPort credencialRepositoryPort, AuditoriaUseCase auditoriaUseCase) {
        this.credencialRepositoryPort = credencialRepositoryPort;
        this.auditoriaUseCase = auditoriaUseCase;
    }

    @Override
    public List<Credencial> listarPorCliente(Long clienteId) {
        List<Credencial> credenciais = credencialRepositoryPort.findByClienteId(clienteId);
        // Garante que a senha nunca sai numa listagem, mesmo que o adapter
        // por baixo tenha descriptografado ao carregar do banco.
        credenciais.forEach(c -> c.setSenha(null));
        return credenciais;
    }

    @Override
    @Transactional
    public Credencial adicionar(Long clienteId, Credencial credencial, UsuarioResumo usuarioLogado) {
        credencial.setClienteId(clienteId);
        credencial.setCriadoPor(usuarioLogado);
        credencial.setCriadoEm(LocalDateTime.now());
        credencial.setAtualizadoPor(usuarioLogado);
        credencial.setAtualizadoEm(LocalDateTime.now());

        Credencial salva = credencialRepositoryPort.save(credencial);

        auditoriaUseCase.registrar(usuarioLogado, "CREDENCIAL", salva.getId(), AcaoAuditoria.CRIACAO,
                "Credencial adicionada: " + salva.getSistema());

        salva.setSenha(null);
        return salva;
    }

    @Override
    @Transactional
    public void remover(Long credencialId, UsuarioResumo usuarioLogado) {
        Credencial credencial = credencialRepositoryPort.findById(credencialId)
                .orElseThrow(() -> new CredencialNaoEncontradaException(credencialId));

        credencialRepositoryPort.deleteById(credencialId);

        auditoriaUseCase.registrar(usuarioLogado, "CREDENCIAL", credencialId, AcaoAuditoria.EXCLUSAO,
                "Credencial removida: " + credencial.getSistema());
    }

    @Override
    @Transactional
    public String revelarSenha(Long credencialId, UsuarioResumo usuarioLogado) {
        Credencial credencial = credencialRepositoryPort.findById(credencialId)
                .orElseThrow(() -> new CredencialNaoEncontradaException(credencialId));

        auditoriaUseCase.registrar(usuarioLogado, "CREDENCIAL", credencialId, AcaoAuditoria.VISUALIZACAO_SENHA,
                "Senha visualizada: " + credencial.getSistema());

        return credencial.getSenha();
    }
}
