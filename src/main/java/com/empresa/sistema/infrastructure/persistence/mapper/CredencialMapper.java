package com.empresa.sistema.infrastructure.persistence.mapper;

import com.empresa.sistema.domain.model.Credencial;
import com.empresa.sistema.infrastructure.persistence.entity.ClienteJpaEntity;
import com.empresa.sistema.infrastructure.persistence.entity.CredencialJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CredencialMapper {

    private final UsuarioMapper usuarioMapper;

    public CredencialMapper(UsuarioMapper usuarioMapper) {
        this.usuarioMapper = usuarioMapper;
    }

    public Credencial toDomain(CredencialJpaEntity entity) {
        Credencial credencial = new Credencial();
        credencial.setId(entity.getId());
        credencial.setClienteId(entity.getCliente().getId());
        credencial.setSistema(entity.getSistema());
        credencial.setUrlPortal(entity.getUrlPortal());
        credencial.setUsuarioPortal(entity.getUsuarioPortal());
        credencial.setSenha(entity.getSenha()); // ja vem descriptografada pelo converter
        credencial.setObservacoes(entity.getObservacoes());
        credencial.setCriadoEm(entity.getCriadoEm());
        credencial.setCriadoPor(usuarioMapper.toResumo(entity.getCriadoPor()));
        credencial.setAtualizadoEm(entity.getAtualizadoEm());
        credencial.setAtualizadoPor(usuarioMapper.toResumo(entity.getAtualizadoPor()));
        return credencial;
    }

    public void aplicarNaEntidade(Credencial credencial, CredencialJpaEntity entity) {
        // Referencia so com o ID -- nao precisa carregar o Cliente inteiro so pra gravar o vinculo.
        ClienteJpaEntity clienteRef = new ClienteJpaEntity();
        clienteRef.setId(credencial.getClienteId());
        entity.setCliente(clienteRef);

        entity.setSistema(credencial.getSistema());
        entity.setUrlPortal(credencial.getUrlPortal());
        entity.setUsuarioPortal(credencial.getUsuarioPortal());
        entity.setSenha(credencial.getSenha());
        entity.setObservacoes(credencial.getObservacoes());
        entity.setCriadoEm(credencial.getCriadoEm());
        entity.setAtualizadoEm(credencial.getAtualizadoEm());
        entity.setCriadoPor(credencial.getCriadoPor() != null
                ? usuarioMapper.referenciaPorId(credencial.getCriadoPor().getId()) : null);
        entity.setAtualizadoPor(credencial.getAtualizadoPor() != null
                ? usuarioMapper.referenciaPorId(credencial.getAtualizadoPor().getId()) : null);
    }
}
