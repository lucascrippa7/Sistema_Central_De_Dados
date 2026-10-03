package com.empresa.sistema.application.service;

import com.empresa.sistema.application.port.in.AuditoriaUseCase;
import com.empresa.sistema.application.port.in.NotaFiscalEntradaUseCase.ArquivoImportado;
import com.empresa.sistema.application.port.in.NotaFiscalSaidaUseCase;
import com.empresa.sistema.application.port.out.LeitorXmlNfeSaidaPort;
import com.empresa.sistema.application.port.out.NotaFiscalSaidaRepositoryPort;
import com.empresa.sistema.domain.exception.NotaFiscalNaoEncontradaException;
import com.empresa.sistema.domain.exception.XmlNfeInvalidoException;
import com.empresa.sistema.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/** Mesma logica de importacao em lote de NotaFiscalEntradaApplicationService, so que alimenta o faturamento em vez do credito. */
@Service
public class NotaFiscalSaidaApplicationService implements NotaFiscalSaidaUseCase {

    private final NotaFiscalSaidaRepositoryPort notaFiscalSaidaRepositoryPort;
    private final LeitorXmlNfeSaidaPort leitorXmlNfeSaidaPort;
    private final AuditoriaUseCase auditoriaUseCase;

    public NotaFiscalSaidaApplicationService(NotaFiscalSaidaRepositoryPort notaFiscalSaidaRepositoryPort,
                                              LeitorXmlNfeSaidaPort leitorXmlNfeSaidaPort,
                                              AuditoriaUseCase auditoriaUseCase) {
        this.notaFiscalSaidaRepositoryPort = notaFiscalSaidaRepositoryPort;
        this.leitorXmlNfeSaidaPort = leitorXmlNfeSaidaPort;
        this.auditoriaUseCase = auditoriaUseCase;
    }

    @Transactional
    @Override
    public ResultadoImportacaoNfe importar(Long clienteId, List<ArquivoImportado> arquivos, UsuarioResumo usuarioLogado) {
        ResultadoImportacaoNfe resultado = new ResultadoImportacaoNfe();

        List<ArquivoImportado> arquivosXml = new ArrayList<>();
        for (ArquivoImportado arquivo : arquivos) {
            if (ehZip(arquivo.nomeArquivo())) {
                arquivosXml.addAll(extrairXmlsDoZip(arquivo));
            } else {
                arquivosXml.add(arquivo);
            }
        }

        int importadosNesteLote = 0;
        for (ArquivoImportado arquivoXml : arquivosXml) {
            try {
                NotaFiscalSaida nota = leitorXmlNfeSaidaPort.lerSaida(arquivoXml.conteudo(), arquivoXml.nomeArquivo());

                if (notaFiscalSaidaRepositoryPort.existsByClienteIdAndChaveAcesso(clienteId, nota.getChaveAcesso())) {
                    resultado.registrarDuplicado(arquivoXml.nomeArquivo());
                    continue;
                }

                nota.setClienteId(clienteId);
                nota.setImportadoEm(LocalDateTime.now());
                nota.setImportadoPor(usuarioLogado);

                notaFiscalSaidaRepositoryPort.save(nota);
                resultado.registrarSucesso();
                importadosNesteLote++;
            } catch (XmlNfeInvalidoException e) {
                resultado.registrarErro(arquivoXml.nomeArquivo(), e.getMessage());
            } catch (Exception e) {
                resultado.registrarErro(arquivoXml.nomeArquivo(), "erro inesperado ao processar (" + e.getMessage() + ")");
            }
        }

        auditoriaUseCase.registrar(usuarioLogado, "CLIENTE", clienteId, AcaoAuditoria.EDICAO,
                "Importacao de NF-e de venda: " + importadosNesteLote + " nota(s) importada(s) de " + arquivosXml.size() + " arquivo(s) processado(s)"
                        + (resultado.isTudoOk() ? "" : ", " + resultado.getErros().size() + " com problema"));

        return resultado;
    }

    private boolean ehZip(String nomeArquivo) {
        return nomeArquivo != null && nomeArquivo.toLowerCase().endsWith(".zip");
    }

    private List<ArquivoImportado> extrairXmlsDoZip(ArquivoImportado zip) {
        List<ArquivoImportado> extraidos = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zip.conteudo()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory() || !entry.getName().toLowerCase().endsWith(".xml")) {
                    continue;
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                zis.transferTo(baos);
                extraidos.add(new ArquivoImportado(entry.getName(), baos.toByteArray()));
            }
        } catch (Exception e) {
            extraidos.add(new ArquivoImportado(zip.nomeArquivo(), new byte[0]));
        }
        return extraidos;
    }

    @Override
    public List<NotaFiscalSaida> listarPorCliente(Long clienteId) {
        return notaFiscalSaidaRepositoryPort.findByClienteId(clienteId);
    }

    @Transactional
    @Override
    public void remover(Long notaFiscalId, UsuarioResumo usuarioLogado) {
        NotaFiscalSaida nota = notaFiscalSaidaRepositoryPort.findById(notaFiscalId)
                .orElseThrow(() -> new NotaFiscalNaoEncontradaException(notaFiscalId));

        notaFiscalSaidaRepositoryPort.deleteById(notaFiscalId);

        auditoriaUseCase.registrar(usuarioLogado, "CLIENTE", nota.getClienteId(), AcaoAuditoria.EDICAO,
                "Nota fiscal de venda removida: numero " + nota.getNumero() + " (chave " + nota.getChaveAcesso() + ")");
    }
}
