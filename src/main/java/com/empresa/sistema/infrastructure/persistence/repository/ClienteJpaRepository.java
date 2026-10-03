package com.empresa.sistema.infrastructure.persistence.repository;

import com.empresa.sistema.infrastructure.persistence.entity.ClienteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClienteJpaRepository extends JpaRepository<ClienteJpaEntity, Long>,
        JpaSpecificationExecutor<ClienteJpaEntity> {
    // findAll(Specification, Sort) ja vem pronto de JpaSpecificationExecutor --
    // nao precisa redeclarar aqui.
    //
    // Nota: a planilha de clientes (com CNAEs/codigos de servico abertos)
    // carrega essas duas listas com LAZY simples, uma consulta por cliente
    // (N+1). Ja tentamos SUBSELECT pra otimizar isso, mas quebrou o
    // "adicionar CNAE" -- revertido ate termos tempo de investigar com
    // calma uma alternativa que nao arrisque a funcionalidade principal.
}
