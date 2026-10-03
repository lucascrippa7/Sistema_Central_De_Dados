package com.empresa.sistema.application.service;

import com.empresa.sistema.application.port.in.AuditoriaUseCase;
import com.empresa.sistema.application.port.out.LogAuditoriaRepositoryPort;
import com.empresa.sistema.domain.model.AcaoAuditoria;
import com.empresa.sistema.domain.model.FiltroAuditoria;
import com.empresa.sistema.domain.model.LogAuditoria;
import com.empresa.sistema.domain.model.UsuarioResumo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditoriaApplicationService implements AuditoriaUseCase {

    private final LogAuditoriaRepositoryPort logAuditoriaRepositoryPort;

    public AuditoriaApplicationService(LogAuditoriaRepositoryPort logAuditoriaRepositoryPort) {
        this.logAuditoriaRepositoryPort = logAuditoriaRepositoryPort;
    }

    @Override
    public void registrar(UsuarioResumo usuario, String entidade, Long entidadeId, AcaoAuditoria acao, String detalhes) {
        LogAuditoria log = new LogAuditoria();
        log.setUsuario(usuario);
        log.setEntidade(entidade);
        log.setEntidadeId(entidadeId);
        log.setAcao(acao);
        log.setDetalhes(detalhes);
        logAuditoriaRepositoryPort.save(log);
    }

    @Override
    public List<LogAuditoria> listar(FiltroAuditoria filtro) {
        return logAuditoriaRepositoryPort.buscar(filtro);
    }
}
