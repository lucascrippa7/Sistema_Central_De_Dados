package com.empresa.sistema.infrastructure.persistence.specification;

import com.empresa.sistema.domain.model.AcaoAuditoria;
import com.empresa.sistema.domain.model.FiltroAuditoria;
import com.empresa.sistema.infrastructure.persistence.entity.LogAuditoriaJpaEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Mesmo espirito do ClienteSpecifications: cada metodo aqui e um pedaco de
 * "WHERE" opcional -- se o campo do filtro vier nulo, o metodo devolve null
 * e o Spring Data ignora esse pedaco.
 */
public class LogAuditoriaSpecifications {

    private LogAuditoriaSpecifications() {
    }

    public static Specification<LogAuditoriaJpaEntity> apartirDoFiltro(FiltroAuditoria filtro) {
        return Specification
                .where(comEntidade(filtro.getEntidade()))
                .and(comAcao(filtro.getAcao()))
                .and(comUsuarioId(filtro.getUsuarioId()))
                .and(comPeriodo(filtro.getDataInicio(), filtro.getDataFim()));
    }

    private static Specification<LogAuditoriaJpaEntity> comEntidade(String entidade) {
        if (entidade == null || entidade.isBlank()) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("entidade"), entidade);
    }

    private static Specification<LogAuditoriaJpaEntity> comAcao(AcaoAuditoria acao) {
        if (acao == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("acao"), acao);
    }

    private static Specification<LogAuditoriaJpaEntity> comUsuarioId(Long usuarioId) {
        if (usuarioId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("usuario").get("id"), usuarioId);
    }

    /** dataInicio/dataFim sao inclusivas -- dataFim cobre o dia inteiro (ate 23:59:59). */
    private static Specification<LogAuditoriaJpaEntity> comPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null && dataFim == null) {
            return null;
        }
        return (root, query, cb) -> {
            if (dataInicio != null && dataFim != null) {
                return cb.between(root.get("dataHora"), dataInicio.atStartOfDay(), dataFim.atTime(23, 59, 59));
            }
            if (dataInicio != null) {
                return cb.greaterThanOrEqualTo(root.get("dataHora"), dataInicio.atStartOfDay());
            }
            LocalDateTime fimDoDia = dataFim.atTime(23, 59, 59);
            return cb.lessThanOrEqualTo(root.get("dataHora"), fimDoDia);
        };
    }
}
