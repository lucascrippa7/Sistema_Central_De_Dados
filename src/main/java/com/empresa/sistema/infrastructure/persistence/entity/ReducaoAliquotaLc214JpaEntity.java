package com.empresa.sistema.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "reducao_aliquota_lc214")
@Getter
@Setter
@NoArgsConstructor
public class ReducaoAliquotaLc214JpaEntity {

    @Id
    @Column(length = 30)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String categoria;

    @Column(name = "percentual_reducao", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentualReducao;

    @Column(name = "base_legal", nullable = false, length = 100)
    private String baseLegal;

    @Column(nullable = false, length = 500)
    private String descricao;
}
