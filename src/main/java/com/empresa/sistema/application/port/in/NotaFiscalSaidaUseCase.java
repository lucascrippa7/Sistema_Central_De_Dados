package com.empresa.sistema.application.port.in;

import com.empresa.sistema.domain.model.NotaFiscalSaida;
import com.empresa.sistema.domain.model.ResultadoImportacaoNfe;
import com.empresa.sistema.domain.model.UsuarioResumo;

import java.util.List;

public interface NotaFiscalSaidaUseCase {

    /** Mesma logica de lote de NotaFiscalEntradaUseCase: aceita .xml e .zip misturados. */
    ResultadoImportacaoNfe importar(Long clienteId, List<NotaFiscalEntradaUseCase.ArquivoImportado> arquivos, UsuarioResumo usuarioLogado);

    List<NotaFiscalSaida> listarPorCliente(Long clienteId);

    void remover(Long notaFiscalId, UsuarioResumo usuarioLogado);
}
