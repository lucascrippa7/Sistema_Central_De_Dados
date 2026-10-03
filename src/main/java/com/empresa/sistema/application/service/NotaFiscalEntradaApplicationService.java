package com.empresa.sistema.application.service;

import com.empresa.sistema.application.port.in.AuditoriaUseCase;
import com.empresa.sistema.application.port.in.NotaFiscalEntradaUseCase;
import com.empresa.sistema.application.port.out.LeitorXmlNfePort;
import com.empresa.sistema.application.port.out.NotaFiscalEntradaRepositoryPort;
import com.empresa.sistema.domain.exception.NotaFiscalNaoEncontradaException;
import com.empresa.sistema.domain.exception.XmlNfeInvalidoException;
import com.empresa.sistema.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class NotaFiscalEntradaApplicationService implements NotaFiscalEntradaUseCase {

    private final NotaFiscalEntradaRepositoryPort notaFiscalEntradaRepositoryPort;
    private final LeitorXmlNfePort leitorXmlNfePort;
    private final AuditoriaUseCase auditoriaUseCase;

    public NotaFiscalEntradaApplicationService(NotaFiscalEntradaRepositoryPort notaFiscalEntradaRepositoryPort,
                                                LeitorXmlNfePort leitorXmlNfePort,
                                                AuditoriaUseCase auditoriaUseCase) {
        this.notaFiscalEntradaRepositoryPort = notaFiscalEntradaRepositoryPort;
        this.leitorXmlNfePort = leitorXmlNfePort;
        this.auditoriaUseCase = auditoriaUseCase;
    }

    @Transactional
    @Override
    public ResultadoImportacaoNfe importar(Long clienteId, List<ArquivoImportado> arquivos, UsuarioResumo usuarioLogado) {
        ResultadoImportacaoNfe resultado = new ResultadoImportacaoNfe();

        // Cada arquivo enviado pode ser um .xml direto ou um .zip com varios .xml
        // dentro -- os dois formatos podem vir misturados no mesmo lote.
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
                NotaFiscalEntrada nota = leitorXmlNfePort.ler(arquivoXml.conteudo(), arquivoXml.nomeArquivo());

                if (notaFiscalEntradaRepositoryPort.existsByClienteIdAndChaveAcesso(clienteId, nota.getChaveAcesso())) {
                    resultado.registrarDuplicado(arquivoXml.nomeArquivo());
                    continue;
                }

                nota.setClienteId(clienteId);
                nota.setImportadoEm(LocalDateTime.now());
                nota.setImportadoPor(usuarioLogado);
                nota.getItens().forEach(RegrasCreditoTributario::aplicar);

                notaFiscalEntradaRepositoryPort.save(nota);
                resultado.registrarSucesso();
                importadosNesteLote++;
            } catch (XmlNfeInvalidoException e) {
                resultado.registrarErro(arquivoXml.nomeArquivo(), e.getMessage());
            } catch (Exception e) {
                resultado.registrarErro(arquivoXml.nomeArquivo(), "erro inesperado ao processar (" + e.getMessage() + ")");
            }
        }

        auditoriaUseCase.registrar(usuarioLogado, "CLIENTE", clienteId, AcaoAuditoria.EDICAO,
                "Importacao de NF-e: " + importadosNesteLote + " nota(s) importada(s) de " + arquivosXml.size() + " arquivo(s) processado(s)"
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
    public List<NotaFiscalEntrada> listarPorCliente(Long clienteId) {
        return notaFiscalEntradaRepositoryPort.findByClienteId(clienteId);
    }

    @Override
    public ResumoCreditoTributario calcularResumo(Long clienteId, LocalDate periodoInicio, LocalDate periodoFim) {
        List<NotaFiscalEntrada> notas = notaFiscalEntradaRepositoryPort.findByClienteIdAndPeriodo(clienteId, periodoInicio, periodoFim);

        ResumoCreditoTributario resumo = new ResumoCreditoTributario();
        resumo.setPeriodoInicio(periodoInicio);
        resumo.setPeriodoFim(periodoFim);
        resumo.setQuantidadeNotas(notas.size());
        for (NotaFiscalEntrada nota : notas) {
            resumo.setTotalCreditoIcms(resumo.getTotalCreditoIcms().add(nota.getTotalCreditoIcms()));
            resumo.setTotalCreditoPis(resumo.getTotalCreditoPis().add(nota.getTotalCreditoPis()));
            resumo.setTotalCreditoCofins(resumo.getTotalCreditoCofins().add(nota.getTotalCreditoCofins()));
        }
        return resumo;
    }

    @Transactional
    @Override
    public void remover(Long notaFiscalId, UsuarioResumo usuarioLogado) {
        NotaFiscalEntrada nota = notaFiscalEntradaRepositoryPort.findById(notaFiscalId)
                .orElseThrow(() -> new NotaFiscalNaoEncontradaException(notaFiscalId));

        notaFiscalEntradaRepositoryPort.deleteById(notaFiscalId);

        auditoriaUseCase.registrar(usuarioLogado, "CLIENTE", nota.getClienteId(), AcaoAuditoria.EDICAO,
                "Nota fiscal removida: numero " + nota.getNumero() + " (chave " + nota.getChaveAcesso() + ")");
    }
}
