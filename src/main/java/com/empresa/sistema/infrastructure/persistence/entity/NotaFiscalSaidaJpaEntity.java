package com.empresa.sistema.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "notas_fiscais_saida")
@Getter
@Setter
@NoArgsConstructor
public class NotaFiscalSaidaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteJpaEntity cliente;

    @Column(name = "chave_acesso", nullable = false, length = 44)
    private String chaveAcesso;

    @Column(length = 20)
    private String numero;

    @Column(length = 10)
    private String serie;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(name = "destinatario_cnpj", length = 18)
    private String destinatarioCnpj;

    @Column(name = "destinatario_razao_social", length = 200)
    private String destinatarioRazaoSocial;

    @Column(name = "valor_total_nota", precision = 15, scale = 2)
    private BigDecimal valorTotalNota = BigDecimal.ZERO;

    @Column(name = "nome_arquivo_original", length = 255)
    private String nomeArquivoOriginal;

    @Column(name = "importado_em", nullable = false, updatable = false)
    private LocalDateTime importadoEm = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "importado_por_id")
    private UsuarioJpaEntity importadoPor;
}
