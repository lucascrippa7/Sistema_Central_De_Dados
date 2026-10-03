package com.empresa.sistema.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cliente_codigo_servico")
@Getter
@Setter
@NoArgsConstructor
public class ClienteCodigoServicoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteJpaEntity cliente;

    @Column(nullable = false, length = 30)
    private String codigo;

    @Column(length = 255)
    private String descricao;
}
