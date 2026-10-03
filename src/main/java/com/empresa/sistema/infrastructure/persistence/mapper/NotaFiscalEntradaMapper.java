package com.empresa.sistema.infrastructure.persistence.mapper;

import com.empresa.sistema.domain.model.ItemNotaFiscal;
import com.empresa.sistema.domain.model.NotaFiscalEntrada;
import com.empresa.sistema.infrastructure.persistence.entity.ClienteJpaEntity;
import com.empresa.sistema.infrastructure.persistence.entity.ItemNotaFiscalJpaEntity;
import com.empresa.sistema.infrastructure.persistence.entity.NotaFiscalEntradaJpaEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class NotaFiscalEntradaMapper {

    private final UsuarioMapper usuarioMapper;

    public NotaFiscalEntradaMapper(UsuarioMapper usuarioMapper) {
        this.usuarioMapper = usuarioMapper;
    }

    public NotaFiscalEntrada toDomain(NotaFiscalEntradaJpaEntity entity) {
        NotaFiscalEntrada nota = new NotaFiscalEntrada();
        nota.setId(entity.getId());
        nota.setClienteId(entity.getCliente().getId());
        nota.setChaveAcesso(entity.getChaveAcesso());
        nota.setNumero(entity.getNumero());
        nota.setSerie(entity.getSerie());
        nota.setDataEmissao(entity.getDataEmissao());
        nota.setFornecedorCnpj(entity.getFornecedorCnpj());
        nota.setFornecedorRazaoSocial(entity.getFornecedorRazaoSocial());
        nota.setValorTotalNota(entity.getValorTotalNota());
        nota.setNomeArquivoOriginal(entity.getNomeArquivoOriginal());
        nota.setImportadoEm(entity.getImportadoEm());
        nota.setImportadoPor(usuarioMapper.toResumo(entity.getImportadoPor()));
        nota.setItens(entity.getItens().stream().map(this::itemParaDomain).collect(Collectors.toList()));
        return nota;
    }

    /** Nota fiscal importada nunca e editada -- so criada (importacao) ou removida. */
    public NotaFiscalEntradaJpaEntity paraNovaEntidade(NotaFiscalEntrada nota) {
        NotaFiscalEntradaJpaEntity entity = new NotaFiscalEntradaJpaEntity();
        ClienteJpaEntity clienteRef = new ClienteJpaEntity();
        clienteRef.setId(nota.getClienteId());
        entity.setCliente(clienteRef);
        entity.setChaveAcesso(nota.getChaveAcesso());
        entity.setNumero(nota.getNumero());
        entity.setSerie(nota.getSerie());
        entity.setDataEmissao(nota.getDataEmissao());
        entity.setFornecedorCnpj(nota.getFornecedorCnpj());
        entity.setFornecedorRazaoSocial(nota.getFornecedorRazaoSocial());
        entity.setValorTotalNota(nota.getValorTotalNota());
        entity.setNomeArquivoOriginal(nota.getNomeArquivoOriginal());
        entity.setImportadoEm(nota.getImportadoEm());
        entity.setImportadoPor(nota.getImportadoPor() != null
                ? usuarioMapper.referenciaPorId(nota.getImportadoPor().getId()) : null);

        for (ItemNotaFiscal item : nota.getItens()) {
            ItemNotaFiscalJpaEntity itemEntity = new ItemNotaFiscalJpaEntity();
            itemEntity.setNotaFiscal(entity);
            itemEntity.setNumeroItem(item.getNumeroItem());
            itemEntity.setDescricaoProduto(item.getDescricaoProduto());
            itemEntity.setNcm(item.getNcm());
            itemEntity.setCfop(item.getCfop());
            itemEntity.setCstIcms(item.getCstIcms());
            itemEntity.setCstPis(item.getCstPis());
            itemEntity.setCstCofins(item.getCstCofins());
            itemEntity.setValorProduto(item.getValorProduto());
            itemEntity.setValorIcms(item.getValorIcms());
            itemEntity.setValorPis(item.getValorPis());
            itemEntity.setValorCofins(item.getValorCofins());
            itemEntity.setCreditoIcms(item.getCreditoIcms());
            itemEntity.setCreditoPis(item.getCreditoPis());
            itemEntity.setCreditoCofins(item.getCreditoCofins());
            entity.getItens().add(itemEntity);
        }
        return entity;
    }

    private ItemNotaFiscal itemParaDomain(ItemNotaFiscalJpaEntity entity) {
        ItemNotaFiscal item = new ItemNotaFiscal();
        item.setId(entity.getId());
        item.setNumeroItem(entity.getNumeroItem());
        item.setDescricaoProduto(entity.getDescricaoProduto());
        item.setNcm(entity.getNcm());
        item.setCfop(entity.getCfop());
        item.setCstIcms(entity.getCstIcms());
        item.setCstPis(entity.getCstPis());
        item.setCstCofins(entity.getCstCofins());
        item.setValorProduto(entity.getValorProduto());
        item.setValorIcms(entity.getValorIcms());
        item.setValorPis(entity.getValorPis());
        item.setValorCofins(entity.getValorCofins());
        item.setCreditoIcms(entity.getCreditoIcms());
        item.setCreditoPis(entity.getCreditoPis());
        item.setCreditoCofins(entity.getCreditoCofins());
        return item;
    }
}
