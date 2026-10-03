package com.empresa.sistema.infrastructure.persistence.mapper;

import com.empresa.sistema.domain.model.LogAuditoria;
import com.empresa.sistema.infrastructure.persistence.entity.LogAuditoriaJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class LogAuditoriaMapper {

    private final UsuarioMapper usuarioMapper;

    public LogAuditoriaMapper(UsuarioMapper usuarioMapper) {
        this.usuarioMapper = usuarioMapper;
    }

    public LogAuditoriaJpaEntity toEntity(LogAuditoria log) {
        LogAuditoriaJpaEntity entity = new LogAuditoriaJpaEntity();
        entity.setUsuario(log.getUsuario() != null ? usuarioMapper.referenciaPorId(log.getUsuario().getId()) : null);
        entity.setEntidade(log.getEntidade());
        entity.setEntidadeId(log.getEntidadeId());
        entity.setAcao(log.getAcao());
        entity.setDetalhes(log.getDetalhes());
        entity.setDataHora(log.getDataHora());
        return entity;
    }

    public LogAuditoria toDomain(LogAuditoriaJpaEntity entity) {
        LogAuditoria log = new LogAuditoria();
        log.setId(entity.getId());
        log.setUsuario(usuarioMapper.toResumo(entity.getUsuario()));
        log.setEntidade(entity.getEntidade());
        log.setEntidadeId(entity.getEntidadeId());
        log.setAcao(entity.getAcao());
        log.setDetalhes(entity.getDetalhes());
        log.setDataHora(entity.getDataHora());
        return log;
    }
}
