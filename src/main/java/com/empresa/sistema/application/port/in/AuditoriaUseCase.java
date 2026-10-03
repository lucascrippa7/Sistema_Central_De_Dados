package com.empresa.sistema.application.port.in;

import com.empresa.sistema.domain.model.AcaoAuditoria;
import com.empresa.sistema.domain.model.FiltroAuditoria;
import com.empresa.sistema.domain.model.LogAuditoria;
import com.empresa.sistema.domain.model.UsuarioResumo;

import java.util.List;

public interface AuditoriaUseCase {
    void registrar(UsuarioResumo usuario, String entidade, Long entidadeId, AcaoAuditoria acao, String detalhes);

    /** As entradas mais recentes que batem com o filtro (limitadas para nao sobrecarregar a tela -- veja o adapter). */
    List<LogAuditoria> listar(FiltroAuditoria filtro);
}
