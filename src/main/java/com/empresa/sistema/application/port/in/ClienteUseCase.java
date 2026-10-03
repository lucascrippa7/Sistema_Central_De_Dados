package com.empresa.sistema.application.port.in;

import com.empresa.sistema.domain.model.Cliente;
import com.empresa.sistema.domain.model.ClienteCnae;
import com.empresa.sistema.domain.model.ClienteCodigoServico;
import com.empresa.sistema.domain.model.FiltroCliente;
import com.empresa.sistema.domain.model.PaginaCliente;
import com.empresa.sistema.domain.model.UsuarioResumo;

import java.util.List;

/**
 * Casos de uso de Cliente. Esta interface e a "porta de entrada": e o que
 * a camada de infraestrutura (controllers) enxerga -- ela nao sabe, e nao
 * precisa saber, como isso e implementado por baixo.
 */
public interface ClienteUseCase {

    List<Cliente> listar(FiltroCliente filtro);

    /** Paginado -- usado so na tela de lista. Pagina comeca em 0. */
    PaginaCliente listarPaginado(FiltroCliente filtro, int pagina, int tamanhoPagina);

    Cliente buscarPorId(Long id);

    Cliente salvar(Cliente cliente, UsuarioResumo usuarioLogado);

    void excluir(Long id, UsuarioResumo usuarioLogado);

    void adicionarCnae(Long clienteId, ClienteCnae novaCnae, UsuarioResumo usuarioLogado);

    void removerCnae(Long clienteId, Long cnaeId, UsuarioResumo usuarioLogado);

    void adicionarCodigoServico(Long clienteId, ClienteCodigoServico novoCodigo, UsuarioResumo usuarioLogado);

    void removerCodigoServico(Long clienteId, Long codigoId, UsuarioResumo usuarioLogado);
}
