package com.empresa.sistema.application.port.out;

import com.empresa.sistema.domain.model.Cliente;
import com.empresa.sistema.domain.model.FiltroCliente;
import com.empresa.sistema.domain.model.PaginaCliente;

import java.util.List;
import java.util.Optional;

/**
 * Porta de saida para persistencia de Cliente. A camada de aplicacao
 * depende SO desta interface -- quem implementa (com JPA, com outra
 * tecnologia, com um mock em teste) e escolha da infraestrutura.
 */
public interface ClienteRepositoryPort {
    List<Cliente> buscar(FiltroCliente filtro);
    PaginaCliente buscarPaginado(FiltroCliente filtro, int pagina, int tamanhoPagina);
    Optional<Cliente> findById(Long id);
    Cliente save(Cliente cliente);
    void deleteById(Long id);
}
