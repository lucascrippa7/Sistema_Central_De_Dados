package com.empresa.sistema.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class LogAuditoria {
    private Long id;
    private UsuarioResumo usuario;

    /** Ex: "CLIENTE", "CREDENCIAL", "USUARIO" */
    private String entidade;
    private Long entidadeId;
    private AcaoAuditoria acao;
    private String detalhes;
    private LocalDateTime dataHora = LocalDateTime.now();
}
