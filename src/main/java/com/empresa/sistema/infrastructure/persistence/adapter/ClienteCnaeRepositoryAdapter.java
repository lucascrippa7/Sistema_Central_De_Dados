package com.empresa.sistema.infrastructure.persistence.adapter;

import com.empresa.sistema.application.port.out.ClienteCnaeRepositoryPort;
import com.empresa.sistema.domain.model.ClienteCnae;
import com.empresa.sistema.infrastructure.persistence.entity.ClienteCnaeJpaEntity;
import com.empresa.sistema.infrastructure.persistence.entity.ClienteJpaEntity;
import com.empresa.sistema.infrastructure.persistence.repository.ClienteCnaeJpaRepository;
import org.springframework.stereotype.Component;

/**
 * Salva/remove um CNAE direto, sem passar pelo Cliente inteiro -- ver o
 * comentario em ClienteCnaeRepositoryPort pra entender por que esse desenho
 * substituiu o antigo (que tinha um bug: adicionar CNAE nao persistia).
 */
@Component
public class ClienteCnaeRepositoryAdapter implements ClienteCnaeRepositoryPort {

    private final ClienteCnaeJpaRepository clienteCnaeJpaRepository;

    public ClienteCnaeRepositoryAdapter(ClienteCnaeJpaRepository clienteCnaeJpaRepository) {
        this.clienteCnaeJpaRepository = clienteCnaeJpaRepository;
    }

    @Override
    public ClienteCnae save(Long clienteId, ClienteCnae cnae) {
        ClienteCnaeJpaEntity entity = new ClienteCnaeJpaEntity();
        ClienteJpaEntity clienteRef = new ClienteJpaEntity();
        clienteRef.setId(clienteId);
        entity.setCliente(clienteRef);
        entity.setCodigo(cnae.getCodigo());
        entity.setDescricao(cnae.getDescricao());
        entity.setPrincipal(cnae.isPrincipal());

        ClienteCnaeJpaEntity salvo = clienteCnaeJpaRepository.save(entity);
        return new ClienteCnae(salvo.getId(), salvo.getCodigo(), salvo.getDescricao(), salvo.isPrincipal());
    }

    @Override
    public void deleteById(Long id) {
        clienteCnaeJpaRepository.deleteById(id);
    }
}
