package com.empresa.sistema.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_faturamento", uniqueConstraints = @UniqueConstraint(columnNames = {"cliente_id", "ano"}))
@Getter
@Setter
@NoArgsConstructor
public class HistoricoFaturamentoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteJpaEntity cliente;

    @Column(nullable = false)
    private int ano;

    @Column(name = "valor_faturamento", precision = 15, scale = 2, nullable = false)
    private BigDecimal valorFaturamento = BigDecimal.ZERO;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "atualizado_por_id")
    private UsuarioJpaEntity atualizadoPor;
}
