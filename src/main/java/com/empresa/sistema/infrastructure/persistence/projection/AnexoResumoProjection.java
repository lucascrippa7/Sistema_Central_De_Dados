package com.empresa.sistema.infrastructure.persistence.projection;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

/**
 * Projecao usada so pra LISTAR anexos (nome, tamanho, data) sem trazer o
 * campo "conteudo" do banco -- que pode ter varios MB por arquivo. O Spring
 * Data gera a query so com as colunas declaradas aqui, entao "conteudo"
 * nunca e lido do banco nessa consulta.
 */
public interface AnexoResumoProjection {
    Long getId();
    String getNomeArquivo();
    String getTipoConteudo();
    long getTamanhoBytes();
    LocalDateTime getCriadoEm();

    @Value("#{target.criadoPor != null ? target.criadoPor.nome : null}")
    String getCriadoPorNome();
}
