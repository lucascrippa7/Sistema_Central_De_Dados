package com.empresa.sistema.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente, como objeto de dominio puro (sem anotacoes de JPA).
 *
 * A anotacao @NotBlank e do Bean Validation (jakarta.validation), um
 * padrao independente de framework -- nao amarra o dominio ao Spring
 * nem ao Hibernate, entao mantivemos aqui para reaproveitar a validacao
 * tanto no formulario web quanto em eventuais outros pontos de entrada.
 */
@Getter
@Setter
@NoArgsConstructor
public class Cliente {

    private Long id;

    @NotBlank(message = "Informe a razao social")
    private String razaoSocial;

    private String nomeFantasia;

    /** So numeros ou formatado (ex: 12.345.678/0001-95) -- guardamos como a pessoa digitou/veio da consulta. */
    private String cnpj;

    private String regimeTributario;
    private String cidade;
    private String estado;
    private String tipoServico;
    private String descricaoServico;

    /** Codigo da categoria de reducao de IBS/CBS aplicavel (LC 214/2025, ver ReducaoAliquotaLc214) -- nulo/"SEM_REDUCAO" = aliquota cheia. Usado no comparativo de regimes (Fase 4). */
    private String categoriaReducaoLc214;

    private boolean ativo = true;

    private String inscricaoMunicipal;
    private String inscricaoEstadual;

    /** Codigo desse cliente em outro sistema usado pela empresa (ex: Dominio). */
    private String codigoSistemaExterno;

    /** O responsavel e sempre um usuario do sistema, nunca texto livre. */
    private UsuarioResumo responsavel;

    private LocalDateTime criadoEm;
    private UsuarioResumo criadoPor;

    private LocalDateTime atualizadoEm;
    private UsuarioResumo atualizadoPor;

    private List<ClienteCnae> cnaes = new ArrayList<>();
    private List<ClienteCodigoServico> codigosServico = new ArrayList<>();
}
