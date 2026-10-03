package com.empresa.sistema.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cliente_cnae")
@Getter
@Setter
@NoArgsConstructor
public class ClienteCnaeJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteJpaEntity cliente;

    @Column(nullable = false, length = 15)
    private String codigo;

    @Column(length = 255)
    private String descricao;

    @Column(nullable = false)
    private boolean principal = false;
}
