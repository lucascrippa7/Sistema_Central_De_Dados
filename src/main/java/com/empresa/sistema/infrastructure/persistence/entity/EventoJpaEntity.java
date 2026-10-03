package com.empresa.sistema.infrastructure.persistence.entity;

import com.empresa.sistema.domain.model.TipoEvento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "eventos")
@Getter
@Setter
@NoArgsConstructor
public class EventoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoEvento tipo;

    @Column(nullable = false)
    private LocalDate data;

    private LocalTime horario;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private ClienteJpaEntity cliente;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioJpaEntity usuario;

    @Column(nullable = false)
    private boolean recorrente = false;

    @Column(name = "serie_id")
    private Long serieId;

    @Column(name = "serie_ativa", nullable = false)
    private boolean serieAtiva = true;

    @Column(name = "dia_mes")
    private Integer diaMes;

    @Column(name = "serie_ano_referencia")
    private Integer serieAnoReferencia;

    @Column(name = "serie_mes_referencia")
    private Integer serieMesReferencia;

    @ElementCollection
    @CollectionTable(name = "evento_lembretes", joinColumns = @JoinColumn(name = "evento_id"))
    @Column(name = "dias_antes")
    private List<Integer> lembretes = new ArrayList<>();

    @Column(name = "notificacao_email_enviada_em")
    private LocalDate notificacaoEmailEnviadaEm;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "criado_por_id")
    private UsuarioJpaEntity criadoPor;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @ManyToOne
    @JoinColumn(name = "atualizado_por_id")
    private UsuarioJpaEntity atualizadoPor;
}

