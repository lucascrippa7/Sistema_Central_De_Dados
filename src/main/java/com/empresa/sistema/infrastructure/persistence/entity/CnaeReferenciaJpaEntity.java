package com.empresa.sistema.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cnae_referencia")
@Getter
@Setter
@NoArgsConstructor
public class CnaeReferenciaJpaEntity {

    @Id
    @Column(length = 15)
    private String codigo;

    @Column(nullable = false, length = 255)
    private String descricao;
}
