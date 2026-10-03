package com.empresa.sistema.application.port.out;

import com.empresa.sistema.domain.model.ClienteCodigoServico;

/** Mesma logica de ClienteCnaeRepositoryPort, so que pra codigo de servico. */
public interface ClienteCodigoServicoRepositoryPort {
    ClienteCodigoServico save(Long clienteId, ClienteCodigoServico codigo);
    void deleteById(Long id);
}
