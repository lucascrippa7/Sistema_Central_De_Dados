package com.empresa.sistema.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Um arquivo anexado a um cliente (contrato social, procuracao, etc).
 * O conteudo fica guardado direto no banco (bytea) -- decisao tomada pra
 * simplificar o backup (tudo num lugar so, junto com o resto dos dados).
 *
 * "conteudo" pode vir nulo dependendo de como o AnexoCliente foi obtido --
 * ver AnexoClienteUseCase para os detalhes de cada metodo.
 */
@Getter
@Setter
@NoArgsConstructor
public class AnexoCliente {
    private Long id;
    private Long clienteId;
    private String nomeArquivo;
    private String tipoConteudo;
    private long tamanhoBytes;
    private byte[] conteudo;
    private LocalDateTime criadoEm;
    private UsuarioResumo criadoPor;
}
