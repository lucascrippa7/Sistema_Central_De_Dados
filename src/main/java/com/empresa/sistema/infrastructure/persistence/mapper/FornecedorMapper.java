package com.empresa.sistema.infrastructure.persistence.mapper;

import com.empresa.sistema.domain.model.Fornecedor;
import com.empresa.sistema.infrastructure.persistence.entity.ClienteJpaEntity;
import com.empresa.sistema.infrastructure.persistence.entity.FornecedorJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class FornecedorMapper {

    private final UsuarioMapper usuarioMapper;

    public FornecedorMapper(UsuarioMapper usuarioMapper) {
        this.usuarioMapper = usuarioMapper;
    }

    public Fornecedor toDomain(FornecedorJpaEntity entity) {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(entity.getId());
        fornecedor.setClienteId(entity.getCliente().getId());
        fornecedor.setCnpj(entity.getCnpj());
        fornecedor.setRazaoSocial(entity.getRazaoSocial());
        fornecedor.setRegimeTributario(entity.getRegimeTributario());
        fornecedor.setSituacaoCadastral(entity.getSituacaoCadastral());
        fornecedor.setUltimaConsultaEm(entity.getUltimaConsultaEm());
        fornecedor.setCriadoEm(entity.getCriadoEm());
        fornecedor.setCriadoPor(usuarioMapper.toResumo(entity.getCriadoPor()));
        return fornecedor;
    }

    /** Usado so na criacao (fornecedor nunca e editado campo a campo, so reconsultado -- ver aplicarNaEntidade). */
    public FornecedorJpaEntity paraNovaEntidade(Fornecedor fornecedor) {
        FornecedorJpaEntity entity = new FornecedorJpaEntity();
        ClienteJpaEntity clienteRef = new ClienteJpaEntity();
        clienteRef.setId(fornecedor.getClienteId());
        entity.setCliente(clienteRef);
        aplicarNaEntidade(fornecedor, entity);
        return entity;
    }

    public void aplicarNaEntidade(Fornecedor fornecedor, FornecedorJpaEntity entity) {
        entity.setCnpj(fornecedor.getCnpj());
        entity.setRazaoSocial(fornecedor.getRazaoSocial());
        entity.setRegimeTributario(fornecedor.getRegimeTributario());
        entity.setSituacaoCadastral(fornecedor.getSituacaoCadastral());
        entity.setUltimaConsultaEm(fornecedor.getUltimaConsultaEm());
        entity.setCriadoEm(fornecedor.getCriadoEm());
        entity.setCriadoPor(fornecedor.getCriadoPor() != null
                ? usuarioMapper.referenciaPorId(fornecedor.getCriadoPor().getId()) : null);
    }
}
