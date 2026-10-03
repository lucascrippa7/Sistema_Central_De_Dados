package com.empresa.sistema.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Fornecedor de um cliente -- Fase 2 do modulo de reforma tributaria.
 * So guarda dados publicos do CNPJ (razao social, situacao cadastral),
 * pra dar um alerta basico de regularidade. Nao calcula credito real
 * ainda (isso e Fase 3, com leitura de XML de NF-e).
 */
@Getter
@Setter
@NoArgsConstructor
public class Fornecedor {
    private Long id;
    private Long clienteId;
    private String cnpj;
    private String razaoSocial;
    private String regimeTributario;

    /** "ATIVA", "BAIXADA", "INAPTA", "SUSPENSA", "NULA" -- ou null se a consulta falhou/nunca rodou. */
    private String situacaoCadastral;

    private LocalDateTime ultimaConsultaEm;
    private LocalDateTime criadoEm;
    private UsuarioResumo criadoPor;

    /** So "ATIVA" e considerada regular -- qualquer outra coisa (ou nao consultada ainda) e sinalizada como risco. */
    public boolean isRegular() {
        return "ATIVA".equalsIgnoreCase(situacaoCadastral);
    }
}
