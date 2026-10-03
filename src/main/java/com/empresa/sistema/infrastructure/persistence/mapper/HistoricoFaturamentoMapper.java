package com.empresa.sistema.infrastructure.persistence.mapper;

import com.empresa.sistema.domain.model.HistoricoFaturamento;
import com.empresa.sistema.infrastructure.persistence.entity.ClienteJpaEntity;
import com.empresa.sistema.infrastructure.persistence.entity.HistoricoFaturamentoJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class HistoricoFaturamentoMapper {

    private final UsuarioMapper usuarioMapper;

    public HistoricoFaturamentoMapper(UsuarioMapper usuarioMapper) {
        this.usuarioMapper = usuarioMapper;
    }

    public HistoricoFaturamento toDomain(HistoricoFaturamentoJpaEntity entity) {
        HistoricoFaturamento historico = new HistoricoFaturamento();
        historico.setId(entity.getId());
        historico.setClienteId(entity.getCliente().getId());
        historico.setAno(entity.getAno());
        historico.setValorFaturamento(entity.getValorFaturamento());
        historico.setAtualizadoEm(entity.getAtualizadoEm());
        historico.setAtualizadoPor(usuarioMapper.toResumo(entity.getAtualizadoPor()));
        return historico;
    }

    public HistoricoFaturamentoJpaEntity paraNovaEntidade(HistoricoFaturamento historico) {
        HistoricoFaturamentoJpaEntity entity = new HistoricoFaturamentoJpaEntity();
        ClienteJpaEntity clienteRef = new ClienteJpaEntity();
        clienteRef.setId(historico.getClienteId());
        entity.setCliente(clienteRef);
        entity.setAno(historico.getAno());
        aplicarNaEntidade(historico, entity);
        return entity;
    }

    public void aplicarNaEntidade(HistoricoFaturamento historico, HistoricoFaturamentoJpaEntity entity) {
        entity.setValorFaturamento(historico.getValorFaturamento());
        entity.setAtualizadoEm(historico.getAtualizadoEm());
        entity.setAtualizadoPor(historico.getAtualizadoPor() != null
                ? usuarioMapper.referenciaPorId(historico.getAtualizadoPor().getId()) : null);
    }
}
