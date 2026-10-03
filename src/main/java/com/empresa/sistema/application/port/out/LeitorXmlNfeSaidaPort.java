package com.empresa.sistema.application.port.out;

import com.empresa.sistema.domain.model.NotaFiscalSaida;

/** Le o XML de uma NF-e de saida (venda) -- mesmo layout da NF-e de entrada, so que le o destinatario em vez do emitente. */
public interface LeitorXmlNfeSaidaPort {
    NotaFiscalSaida lerSaida(byte[] conteudoXml, String nomeArquivo);
}
