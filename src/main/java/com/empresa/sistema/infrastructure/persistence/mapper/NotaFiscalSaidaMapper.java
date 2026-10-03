package com.empresa.sistema.infrastructure.persistence.mapper;

import com.empresa.sistema.domain.model.NotaFiscalSaida;
import com.empresa.sistema.infrastructure.persistence.entity.ClienteJpaEntity;
import com.empresa.sistema.infrastructure.persistence.entity.NotaFiscalSaidaJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class NotaFiscalSaidaMapper {

    private final UsuarioMapper usuarioMapper;

    public NotaFiscalSaidaMapper(UsuarioMapper usuarioMapper) {
        this.usuarioMapper = usuarioMapper;
    }

    public NotaFiscalSaida toDomain(NotaFiscalSaidaJpaEntity entity) {
        NotaFiscalSaida nota = new NotaFiscalSaida();
        nota.setId(entity.getId());
        nota.setClienteId(entity.getCliente().getId());
        nota.setChaveAcesso(entity.getChaveAcesso());
        nota.setNumero(entity.getNumero());
        nota.setSerie(entity.getSerie());
        nota.setDataEmissao(entity.getDataEmissao());
        nota.setDestinatarioCnpj(entity.getDestinatarioCnpj());
        nota.setDestinatarioRazaoSocial(entity.getDestinatarioRazaoSocial());
        nota.setValorTotalNota(entity.getValorTotalNota());
        nota.setNomeArquivoOriginal(entity.getNomeArquivoOriginal());
        nota.setImportadoEm(entity.getImportadoEm());
        nota.setImportadoPor(usuarioMapper.toResumo(entity.getImportadoPor()));
        return nota;
    }

    /** Nota fiscal importada nunca e editada -- so criada (importacao) ou removida. */
    public NotaFiscalSaidaJpaEntity paraNovaEntidade(NotaFiscalSaida nota) {
        NotaFiscalSaidaJpaEntity entity = new NotaFiscalSaidaJpaEntity();
        ClienteJpaEntity clienteRef = new ClienteJpaEntity();
        clienteRef.setId(nota.getClienteId());
        entity.setCliente(clienteRef);
        entity.setChaveAcesso(nota.getChaveAcesso());
        entity.setNumero(nota.getNumero());
        entity.setSerie(nota.getSerie());
        entity.setDataEmissao(nota.getDataEmissao());
        entity.setDestinatarioCnpj(nota.getDestinatarioCnpj());
        entity.setDestinatarioRazaoSocial(nota.getDestinatarioRazaoSocial());
        entity.setValorTotalNota(nota.getValorTotalNota());
        entity.setNomeArquivoOriginal(nota.getNomeArquivoOriginal());
        entity.setImportadoEm(nota.getImportadoEm());
        entity.setImportadoPor(nota.getImportadoPor() != null
                ? usuarioMapper.referenciaPorId(nota.getImportadoPor().getId()) : null);
        return entity;
    }
}
