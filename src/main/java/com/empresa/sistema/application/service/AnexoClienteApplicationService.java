package com.empresa.sistema.application.service;

import com.empresa.sistema.application.port.in.AnexoClienteUseCase;
import com.empresa.sistema.application.port.in.AuditoriaUseCase;
import com.empresa.sistema.application.port.out.AnexoClienteRepositoryPort;
import com.empresa.sistema.domain.exception.AnexoNaoEncontradoException;
import com.empresa.sistema.domain.exception.ArquivoMuitoGrandeException;
import com.empresa.sistema.domain.model.AcaoAuditoria;
import com.empresa.sistema.domain.model.AnexoCliente;
import com.empresa.sistema.domain.model.UsuarioResumo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnexoClienteApplicationService implements AnexoClienteUseCase {

    /** Limite de tamanho por arquivo -- guardamos no banco (bytea), entao precisa de um teto sensato. */
    private static final long LIMITE_BYTES = 10L * 1024 * 1024; // 10 MB

    private final AnexoClienteRepositoryPort anexoClienteRepositoryPort;
    private final AuditoriaUseCase auditoriaUseCase;

    public AnexoClienteApplicationService(AnexoClienteRepositoryPort anexoClienteRepositoryPort,
                                           AuditoriaUseCase auditoriaUseCase) {
        this.anexoClienteRepositoryPort = anexoClienteRepositoryPort;
        this.auditoriaUseCase = auditoriaUseCase;
    }

    @Override
    public List<AnexoCliente> listarPorCliente(Long clienteId) {
        return anexoClienteRepositoryPort.findResumoPorClienteId(clienteId);
    }

    @Override
    public AnexoCliente baixar(Long anexoId) {
        return anexoClienteRepositoryPort.findComConteudoById(anexoId)
                .orElseThrow(() -> new AnexoNaoEncontradoException(anexoId));
    }

    @Transactional
    @Override
    public AnexoCliente adicionar(Long clienteId, String nomeArquivo, String tipoConteudo, byte[] conteudo,
                                   UsuarioResumo usuarioLogado) {
        if (conteudo.length > LIMITE_BYTES) {
            throw new ArquivoMuitoGrandeException(LIMITE_BYTES);
        }

        AnexoCliente anexo = new AnexoCliente();
        anexo.setClienteId(clienteId);
        anexo.setNomeArquivo(nomeArquivo);
        anexo.setTipoConteudo(tipoConteudo);
        anexo.setTamanhoBytes(conteudo.length);
        anexo.setConteudo(conteudo);
        anexo.setCriadoEm(LocalDateTime.now());
        anexo.setCriadoPor(usuarioLogado);

        AnexoCliente salvo = anexoClienteRepositoryPort.save(anexo);

        auditoriaUseCase.registrar(usuarioLogado, "CLIENTE", clienteId, AcaoAuditoria.EDICAO,
                "Anexo adicionado: " + nomeArquivo);

        return salvo;
    }

    @Transactional
    @Override
    public void remover(Long anexoId, UsuarioResumo usuarioLogado) {
        AnexoCliente anexo = anexoClienteRepositoryPort.findComConteudoById(anexoId)
                .orElseThrow(() -> new AnexoNaoEncontradoException(anexoId));

        anexoClienteRepositoryPort.deleteById(anexoId);

        auditoriaUseCase.registrar(usuarioLogado, "CLIENTE", anexo.getClienteId(), AcaoAuditoria.EDICAO,
                "Anexo removido: " + anexo.getNomeArquivo());
    }
}
