package com.empresa.sistema.infrastructure.persistence.repository;

import com.empresa.sistema.infrastructure.persistence.entity.NotaFiscalSaidaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotaFiscalSaidaJpaRepository extends JpaRepository<NotaFiscalSaidaJpaEntity, Long> {

    List<NotaFiscalSaidaJpaEntity> findByClienteIdOrderByDataEmissaoDesc(Long clienteId);

    boolean existsByClienteIdAndChaveAcesso(Long clienteId, String chaveAcesso);

    @Query("select extract(year from n.dataEmissao) as ano, sum(n.valorTotalNota) as total " +
            "from NotaFiscalSaidaJpaEntity n where n.cliente.id = :clienteId group by extract(year from n.dataEmissao)")
    List<Object[]> somarValorTotalPorAno(@Param("clienteId") Long clienteId);

    @Query("select extract(year from n.dataEmissao) as ano, count(n) as quantidade " +
            "from NotaFiscalSaidaJpaEntity n where n.cliente.id = :clienteId group by extract(year from n.dataEmissao)")
    List<Object[]> contarPorAno(@Param("clienteId") Long clienteId);
}
