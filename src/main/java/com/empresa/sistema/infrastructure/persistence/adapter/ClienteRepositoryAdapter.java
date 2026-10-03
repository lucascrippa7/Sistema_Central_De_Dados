package com.empresa.sistema.infrastructure.persistence.adapter;

import com.empresa.sistema.application.port.out.ClienteRepositoryPort;
import com.empresa.sistema.domain.exception.ClienteNaoEncontradoException;
import com.empresa.sistema.domain.model.Cliente;
import com.empresa.sistema.domain.model.FiltroCliente;
import com.empresa.sistema.domain.model.PaginaCliente;
import com.empresa.sistema.infrastructure.persistence.entity.ClienteJpaEntity;
import com.empresa.sistema.infrastructure.persistence.mapper.ClienteMapper;
import com.empresa.sistema.infrastructure.persistence.repository.ClienteJpaRepository;
import com.empresa.sistema.infrastructure.persistence.specification.ClienteSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementacao "de verdade" da porta ClienteRepositoryPort, usando
 * Spring Data JPA por baixo. Esta e a unica classe do sistema que sabe
 * ao mesmo tempo o que e um Cliente de dominio E o que e um
 * ClienteJpaEntity -- ela existe exatamente para fazer essa ponte.
 */
@Component
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository clienteJpaRepository;
    private final ClienteMapper clienteMapper;

    public ClienteRepositoryAdapter(ClienteJpaRepository clienteJpaRepository, ClienteMapper clienteMapper) {
        this.clienteJpaRepository = clienteJpaRepository;
        this.clienteMapper = clienteMapper;
    }

    @Override
    public List<Cliente> buscar(FiltroCliente filtro) {
        return clienteJpaRepository.findAll(ClienteSpecifications.apartirDoFiltro(filtro), Sort.by("razaoSocial").ascending()).stream()
                .map(clienteMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public PaginaCliente buscarPaginado(FiltroCliente filtro, int pagina, int tamanhoPagina) {
        PageRequest paginacao = PageRequest.of(pagina, tamanhoPagina, Sort.by("razaoSocial").ascending());
        Page<ClienteJpaEntity> resultado = clienteJpaRepository.findAll(ClienteSpecifications.apartirDoFiltro(filtro), paginacao);

        List<Cliente> conteudo = resultado.getContent().stream()
                .map(clienteMapper::toDomain)
                .collect(Collectors.toList());

        return new PaginaCliente(conteudo, resultado.getNumber(), resultado.getTotalPages(), resultado.getTotalElements());
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        return clienteJpaRepository.findById(id).map(clienteMapper::toDomain);
    }

    @Override
    public Cliente save(Cliente cliente) {
        ClienteJpaEntity entity;
        if (cliente.getId() == null) {
            entity = new ClienteJpaEntity();
        } else {
            entity = clienteJpaRepository.findById(cliente.getId())
                    .orElseThrow(() -> new ClienteNaoEncontradoException(cliente.getId()));
        }

        clienteMapper.aplicarNaEntidade(cliente, entity);
        ClienteJpaEntity salvo = clienteJpaRepository.save(entity);
        return clienteMapper.toDomain(salvo);
    }

    @Override
    public void deleteById(Long id) {
        clienteJpaRepository.deleteById(id);
    }
}
