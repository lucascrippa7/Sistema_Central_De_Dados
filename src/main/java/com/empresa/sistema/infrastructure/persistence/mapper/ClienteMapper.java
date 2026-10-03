package com.empresa.sistema.infrastructure.persistence.mapper;

import com.empresa.sistema.domain.model.Cliente;
import com.empresa.sistema.domain.model.ClienteCnae;
import com.empresa.sistema.domain.model.ClienteCodigoServico;
import com.empresa.sistema.infrastructure.persistence.entity.ClienteCnaeJpaEntity;
import com.empresa.sistema.infrastructure.persistence.entity.ClienteCodigoServicoJpaEntity;
import com.empresa.sistema.infrastructure.persistence.entity.ClienteJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClienteMapper {

    private final UsuarioMapper usuarioMapper;

    public ClienteMapper(UsuarioMapper usuarioMapper) {
        this.usuarioMapper = usuarioMapper;
    }

    public Cliente toDomain(ClienteJpaEntity entity) {
        if (entity == null) return null;

        Cliente cliente = new Cliente();
        cliente.setId(entity.getId());
        cliente.setRazaoSocial(entity.getRazaoSocial());
        cliente.setNomeFantasia(entity.getNomeFantasia());
        cliente.setCnpj(entity.getCnpj());
        cliente.setRegimeTributario(entity.getRegimeTributario());
        cliente.setCidade(entity.getCidade());
        cliente.setEstado(entity.getEstado());
        cliente.setTipoServico(entity.getTipoServico());
        cliente.setDescricaoServico(entity.getDescricaoServico());
        cliente.setCategoriaReducaoLc214(entity.getCategoriaReducaoLc214());
        cliente.setAtivo(entity.isAtivo());
        cliente.setInscricaoMunicipal(entity.getInscricaoMunicipal());
        cliente.setInscricaoEstadual(entity.getInscricaoEstadual());
        cliente.setCodigoSistemaExterno(entity.getCodigoSistemaExterno());
        cliente.setResponsavel(usuarioMapper.toResumo(entity.getResponsavel()));
        cliente.setCriadoEm(entity.getCriadoEm());
        cliente.setCriadoPor(usuarioMapper.toResumo(entity.getCriadoPor()));
        cliente.setAtualizadoEm(entity.getAtualizadoEm());
        cliente.setAtualizadoPor(usuarioMapper.toResumo(entity.getAtualizadoPor()));

        cliente.setCnaes(entity.getCnaes().stream().map(this::cnaeToDomain).collect(Collectors.toList()));
        cliente.setCodigosServico(entity.getCodigosServico().stream().map(this::codigoToDomain).collect(Collectors.toList()));

        return cliente;
    }

    /**
     * Copia os campos do objeto de dominio para dentro de uma entidade JPA
     * (nova ou ja gerenciada pelo Hibernate). As listas de CNAEs e codigos
     * de servico sao sincronizadas EM CIMA da lista existente na entidade
     * (nunca substituidas por uma lista nova) -- isso e o que faz o
     * orphanRemoval funcionar corretamente e apagar do banco os itens que
     * foram removidos no dominio.
     */
    public void aplicarNaEntidade(Cliente cliente, ClienteJpaEntity entity) {
        entity.setRazaoSocial(cliente.getRazaoSocial());
        entity.setNomeFantasia(cliente.getNomeFantasia());
        entity.setCnpj(cliente.getCnpj());
        entity.setRegimeTributario(cliente.getRegimeTributario());
        entity.setCidade(cliente.getCidade());
        entity.setEstado(cliente.getEstado());
        entity.setTipoServico(cliente.getTipoServico());
        entity.setDescricaoServico(cliente.getDescricaoServico());
        entity.setCategoriaReducaoLc214(cliente.getCategoriaReducaoLc214());
        entity.setAtivo(cliente.isAtivo());
        entity.setInscricaoMunicipal(cliente.getInscricaoMunicipal());
        entity.setInscricaoEstadual(cliente.getInscricaoEstadual());
        entity.setCodigoSistemaExterno(cliente.getCodigoSistemaExterno());
        entity.setResponsavel(cliente.getResponsavel() != null
                ? usuarioMapper.referenciaPorId(cliente.getResponsavel().getId()) : null);
        entity.setCriadoEm(cliente.getCriadoEm());
        entity.setAtualizadoEm(cliente.getAtualizadoEm());

        entity.setCriadoPor(cliente.getCriadoPor() != null
                ? usuarioMapper.referenciaPorId(cliente.getCriadoPor().getId()) : null);
        entity.setAtualizadoPor(cliente.getAtualizadoPor() != null
                ? usuarioMapper.referenciaPorId(cliente.getAtualizadoPor().getId()) : null);

        // CNAEs e codigos de servico NAO sao sincronizados aqui de proposito
        // -- adicionar/remover um item passa direto por
        // ClienteCnaeRepositoryPort/ClienteCodigoServicoRepositoryPort
        // (mesmo padrao ja usado por Credencial e Anexo), sem precisar tocar
        // no Cliente inteiro. O jeito antigo (sincronizar a lista toda a
        // cada salvamento) tinha um bug real: adicionar um CNAE nao
        // persistia. Aqui em aplicarNaEntidade, essas duas listas
        // simplesmente ficam do jeito que ja estao no banco.
    }

    private ClienteCnae cnaeToDomain(ClienteCnaeJpaEntity entity) {
        return new ClienteCnae(entity.getId(), entity.getCodigo(), entity.getDescricao(), entity.isPrincipal());
    }

    private ClienteCodigoServico codigoToDomain(ClienteCodigoServicoJpaEntity entity) {
        return new ClienteCodigoServico(entity.getId(), entity.getCodigo(), entity.getDescricao());
    }
}
