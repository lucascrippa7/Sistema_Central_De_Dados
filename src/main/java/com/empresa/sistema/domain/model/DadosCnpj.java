package com.empresa.sistema.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * O que conseguimos puxar de um CNPJ numa consulta publica (BrasilAPI) --
 * so os campos que o formulario de cliente de fato usa pra preencher
 * sozinho. Nao e o Cliente em si (o usuario ainda confirma/ajusta antes
 * de salvar).
 */
@Getter
@Setter
@NoArgsConstructor
public class DadosCnpj {
    private String cnpj;
    private String razaoSocial;
    private String nomeFantasia;
    private String cidade;
    private String estado;

    /** "MEI" ou "Simples Nacional" quando a Receita informa isso claramente -- senao fica em branco (Lucro Presumido/Real nao dao pra distinguir por aqui). */
    private String regimeTributario;

    /** CNAE principal (com principal=true) + secundarios, como vieram da consulta -- sem id ainda. */
    private List<ClienteCnae> cnaes = new ArrayList<>();

    /** "ATIVA", "BAIXADA", "INAPTA", "SUSPENSA", "NULA" -- como a Receita informa. Null se nao veio na resposta. */
    private String situacaoCadastral;
}
