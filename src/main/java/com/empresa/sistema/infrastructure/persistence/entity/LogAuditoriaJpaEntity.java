package com.empresa.sistema.infrastructure.persistence.entity;

import com.empresa.sistema.domain.model.AcaoAuditoria;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "log_auditoria")
@Getter
@Setter
@NoArgsConstructor
public class LogAuditoriaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioJpaEntity usuario;

    @Column(nullable = false, length = 60)
    private String entidade;

    @Column(name = "entidade_id")
    private Long entidadeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AcaoAuditoria acao;

    @Column(columnDefinition = "TEXT")
    private String detalhes;

    @Column(name = "data_hora", nullable = false, updatable = false)
    private LocalDateTime dataHora = LocalDateTime.now();
}
