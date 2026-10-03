package com.empresa.sistema.infrastructure.persistence.specification;

import com.empresa.sistema.domain.model.FiltroCliente;
import com.empresa.sistema.infrastructure.persistence.entity.ClienteJpaEntity;
import org.springframework.data.jpa.domain.Specification;

/**
 * Cada metodo aqui vira um pedaco de "WHERE" opcional -- se o campo do
 * filtro vier nulo/em branco, o metodo devolve null e o Spring Data
 * simplesmente ignora esse pedaco (nao aplica restricao nenhuma nele).
 * Isso e o que permite combinar qualquer conjunto de filtros sem precisar
 * escrever uma query SQL para cada combinacao possivel.
 */
public class ClienteSpecifications {

    private ClienteSpecifications() {
    }

    public static Specification<ClienteJpaEntity> apartirDoFiltro(FiltroCliente filtro) {
        return Specification
                .where(comTermo(filtro.getTermo()))
                .and(comEstado(filtro.getEstado()))
                .and(comRegimeTributario(filtro.getRegimeTributario()))
                .and(comAtivo(filtro.getAtivo()))
                .and(comCodigoSistemaExterno(filtro.getCodigoSistemaExterno()))
                .and(comResponsavelId(filtro.getResponsavelId()));
    }

    private static Specification<ClienteJpaEntity> comTermo(String termo) {
        if (termo == null || termo.isBlank()) {
            return null;
        }
        String termoBusca = "%" + termo.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("razaoSocial")), termoBusca),
                cb.like(cb.lower(root.get("nomeFantasia")), termoBusca)
        );
    }

    private static Specification<ClienteJpaEntity> comEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return null;
        }
        String estadoBusca = estado.trim().toUpperCase();
        return (root, query, cb) -> cb.equal(cb.upper(root.get("estado")), estadoBusca);
    }

    private static Specification<ClienteJpaEntity> comRegimeTributario(String regimeTributario) {
        if (regimeTributario == null || regimeTributario.isBlank()) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("regimeTributario"), regimeTributario);
    }

    private static Specification<ClienteJpaEntity> comAtivo(Boolean ativo) {
        if (ativo == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("ativo"), ativo);
    }

    private static Specification<ClienteJpaEntity> comCodigoSistemaExterno(String codigoSistemaExterno) {
        if (codigoSistemaExterno == null || codigoSistemaExterno.isBlank()) {
            return null;
        }
        String busca = "%" + codigoSistemaExterno.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("codigoSistemaExterno")), busca);
    }

    private static Specification<ClienteJpaEntity> comResponsavelId(Long responsavelId) {
        if (responsavelId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("responsavel").get("id"), responsavelId);
    }
}
