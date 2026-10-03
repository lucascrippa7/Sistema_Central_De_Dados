package com.empresa.sistema.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteCnae {
    private Long id;
    private String codigo;
    private String descricao;
    private boolean principal;
}
