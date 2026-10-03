package com.empresa.sistema.application.port.out;

import com.empresa.sistema.domain.model.ClienteCnae;

/**
 * Porta de saida dedicada pra CNAE, do mesmo jeito que Credencial e Anexo ja
 * tem a propria porta -- salva/remove UM CNAE por vez, direto, sem precisar
 * buscar o Cliente inteiro e sincronizar a lista toda (esse era o design
 * antigo, que tinha um bug real: adicionar um CNAE nao persistia).
 */
public interface ClienteCnaeRepositoryPort {
    ClienteCnae save(Long clienteId, ClienteCnae cnae);
    void deleteById(Long id);
}
