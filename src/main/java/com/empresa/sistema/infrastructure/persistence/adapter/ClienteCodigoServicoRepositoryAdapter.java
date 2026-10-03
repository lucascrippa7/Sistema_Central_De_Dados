package com.empresa.sistema.infrastructure.persistence.adapter;

import com.empresa.sistema.application.port.out.ClienteCodigoServicoRepositoryPort;
import com.empresa.sistema.domain.model.ClienteCodigoServico;
import com.empresa.sistema.infrastructure.persistence.entity.ClienteCodigoServicoJpaEntity;
import com.empresa.sistema.infrastructure.persistence.entity.ClienteJpaEntity;
import com.empresa.sistema.infrastructure.persistence.repository.ClienteCodigoServicoJpaRepository;
import org.springframework.stereotype.Component;

@Component
public class ClienteCodigoServicoRepositoryAdapter implements ClienteCodigoServicoRepositoryPort {

    private final ClienteCodigoServicoJpaRepository clienteCodigoServicoJpaRepository;

    public ClienteCodigoServicoRepositoryAdapter(ClienteCodigoServicoJpaRepository clienteCodigoServicoJpaRepository) {
        this.clienteCodigoServicoJpaRepository = clienteCodigoServicoJpaRepository;
    }

    @Override
    public ClienteCodigoServico save(Long clienteId, ClienteCodigoServico codigo) {
        ClienteCodigoServicoJpaEntity entity = new ClienteCodigoServicoJpaEntity();
        ClienteJpaEntity clienteRef = new ClienteJpaEntity();
        clienteRef.setId(clienteId);
        entity.setCliente(clienteRef);
        entity.setCodigo(codigo.getCodigo());
        entity.setDescricao(codigo.getDescricao());

        ClienteCodigoServicoJpaEntity salvo = clienteCodigoServicoJpaRepository.save(entity);
        return new ClienteCodigoServico(salvo.getId(), salvo.getCodigo(), salvo.getDescricao());
    }

    @Override
    public void deleteById(Long id) {
        clienteCodigoServicoJpaRepository.deleteById(id);
    }
}
