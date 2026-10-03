package com.empresa.sistema.application.port.out;

import com.empresa.sistema.domain.model.FiltroAuditoria;
import com.empresa.sistema.domain.model.LogAuditoria;

import java.util.List;

public interface LogAuditoriaRepositoryPort {
    void save(LogAuditoria log);

    List<LogAuditoria> buscar(FiltroAuditoria filtro);
}
