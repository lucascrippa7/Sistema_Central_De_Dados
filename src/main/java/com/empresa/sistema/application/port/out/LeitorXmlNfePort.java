package com.empresa.sistema.application.port.out;

import com.empresa.sistema.domain.model.NotaFiscalEntrada;

/**
 * Le o XML de uma NF-e (layout padrao da SEFAZ: nfeProc ou NFe na raiz) e
 * monta o cabecalho + itens. Nao aplica a regra de credito -- isso e feito
 * depois, em NotaFiscalEntradaApplicationService, via RegrasCreditoTributario.
 */
public interface LeitorXmlNfePort {
    NotaFiscalEntrada ler(byte[] conteudoXml, String nomeArquivo);
}
