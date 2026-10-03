package com.empresa.sistema.infrastructure.persistence.mapper;

import com.empresa.sistema.domain.model.AnexoCliente;
import com.empresa.sistema.domain.model.UsuarioResumo;
import com.empresa.sistema.infrastructure.persistence.entity.AnexoClienteJpaEntity;
import com.empresa.sistema.infrastructure.persistence.entity.ClienteJpaEntity;
import com.empresa.sistema.infrastructure.persistence.projection.AnexoResumoProjection;
import org.springframework.stereotype.Component;

@Component
public class AnexoClienteMapper {

    private final UsuarioMapper usuarioMapper;

    public AnexoClienteMapper(UsuarioMapper usuarioMapper) {
        this.usuarioMapper = usuarioMapper;
    }

    /** Usado so na listagem -- "conteudo" fica null de proposito (nao veio na projecao). */
    public AnexoCliente toDomainResumo(AnexoResumoProjection p, Long clienteId) {
        AnexoCliente anexo = new AnexoCliente();
        anexo.setId(p.getId());
        anexo.setClienteId(clienteId);
        anexo.setNomeArquivo(p.getNomeArquivo());
        anexo.setTipoConteudo(p.getTipoConteudo());
        anexo.setTamanhoBytes(p.getTamanhoBytes());
        anexo.setCriadoEm(p.getCriadoEm());
        if (p.getCriadoPorNome() != null) {
            anexo.setCriadoPor(new UsuarioResumo(null, p.getCriadoPorNome(), null));
        }
        return anexo;
    }

    /** Usado na hora de baixar -- aqui sim com o conteudo (byte[]) preenchido. */
    public AnexoCliente toDomainCompleto(AnexoClienteJpaEntity entity) {
        AnexoCliente anexo = new AnexoCliente();
        anexo.setId(entity.getId());
        anexo.setClienteId(entity.getCliente().getId());
        anexo.setNomeArquivo(entity.getNomeArquivo());
        anexo.setTipoConteudo(entity.getTipoConteudo());
        anexo.setTamanhoBytes(entity.getTamanhoBytes());
        anexo.setConteudo(entity.getConteudo());
        anexo.setCriadoEm(entity.getCriadoEm());
        anexo.setCriadoPor(usuarioMapper.toResumo(entity.getCriadoPor()));
        return anexo;
    }

    public AnexoClienteJpaEntity paraNovaEntidade(AnexoCliente anexo) {
        AnexoClienteJpaEntity entity = new AnexoClienteJpaEntity();
        ClienteJpaEntity clienteRef = new ClienteJpaEntity();
        clienteRef.setId(anexo.getClienteId());
        entity.setCliente(clienteRef);
        entity.setNomeArquivo(anexo.getNomeArquivo());
        entity.setTipoConteudo(anexo.getTipoConteudo());
        entity.setTamanhoBytes(anexo.getTamanhoBytes());
        entity.setConteudo(anexo.getConteudo());
        entity.setCriadoEm(anexo.getCriadoEm());
        entity.setCriadoPor(anexo.getCriadoPor() != null
                ? usuarioMapper.referenciaPorId(anexo.getCriadoPor().getId()) : null);
        return entity;
    }
}
