package com.empresa.sistema.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Criterios de busca do log de auditoria. Qualquer campo nulo/em branco
 * significa "sem filtro nesse campo" -- mesmo espirito do FiltroCliente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FiltroAuditoria {
    private String entidade;
    private AcaoAuditoria acao;
    private Long usuarioId;
    private LocalDate dataInicio;
    private LocalDate dataFim;
}
