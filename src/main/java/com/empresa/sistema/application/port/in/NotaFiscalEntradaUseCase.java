package com.empresa.sistema.application.port.in;

import com.empresa.sistema.domain.model.NotaFiscalEntrada;
import com.empresa.sistema.domain.model.ResultadoImportacaoNfe;
import com.empresa.sistema.domain.model.ResumoCreditoTributario;
import com.empresa.sistema.domain.model.UsuarioResumo;

import java.time.LocalDate;
import java.util.List;

public interface NotaFiscalEntradaUseCase {

    /**
     * Importa um lote de arquivos para o cliente. Cada arquivo pode ser um
     * .xml individual ou um .zip contendo varios .xml -- os dois formatos
     * podem ser misturados no mesmo lote. Um arquivo com erro (XML invalido,
     * chave duplicada) nao interrompe os demais -- fica registrado no
     * resultado para o usuario ver o que precisa de atencao.
     */
    ResultadoImportacaoNfe importar(Long clienteId, List<ArquivoImportado> arquivos, UsuarioResumo usuarioLogado);

    List<NotaFiscalEntrada> listarPorCliente(Long clienteId);

    ResumoCreditoTributario calcularResumo(Long clienteId, LocalDate periodoInicio, LocalDate periodoFim);

    void remover(Long notaFiscalId, UsuarioResumo usuarioLogado);

    record ArquivoImportado(String nomeArquivo, byte[] conteudo) {
    }
}
