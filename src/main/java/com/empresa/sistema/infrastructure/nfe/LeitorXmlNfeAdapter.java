package com.empresa.sistema.infrastructure.nfe;

import com.empresa.sistema.application.port.out.LeitorXmlNfePort;
import com.empresa.sistema.application.port.out.LeitorXmlNfeSaidaPort;
import com.empresa.sistema.domain.exception.XmlNfeInvalidoException;
import com.empresa.sistema.domain.model.ItemNotaFiscal;
import com.empresa.sistema.domain.model.NotaFiscalEntrada;
import com.empresa.sistema.domain.model.NotaFiscalSaida;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Le o XML padrao de NF-e (nfeProc/NFe, layout da SEFAZ) e monta o
 * cabecalho + itens da nota. Nao aplica regra de credito -- isso fica em
 * RegrasCreditoTributario, chamada pelo application service.
 *
 * O parser ignora o namespace da NF-e de proposito (getElementsByTagName
 * casa pelo nodeName local quando o XML usa namespace padrao sem prefixo,
 * que e o caso comum dos XMLs emitidos pela SEFAZ) -- isso mantem o codigo
 * simples sem precisar declarar o namespace em todo lugar.
 */
@Component
public class LeitorXmlNfeAdapter implements LeitorXmlNfePort, LeitorXmlNfeSaidaPort {

    private static final DateTimeFormatter DATA_EMISSAO_FORMATO = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public NotaFiscalEntrada ler(byte[] conteudoXml, String nomeArquivo) {
        try {
            Document documento = parseDocumentoSeguro(conteudoXml);

            Element infNFe = primeiroElemento(documento.getDocumentElement(), "infNFe");
            if (infNFe == null) {
                throw new XmlNfeInvalidoException("Nao parece ser um XML de NF-e valido (tag infNFe nao encontrada).");
            }

            Element ide = primeiroElemento(infNFe, "ide");
            Element emit = primeiroElemento(infNFe, "emit");
            Element total = primeiroElemento(infNFe, "total");
            Element icmsTot = total != null ? primeiroElemento(total, "ICMSTot") : null;

            NotaFiscalEntrada nota = new NotaFiscalEntrada();
            nota.setChaveAcesso(extrairChaveAcesso(infNFe));
            nota.setNumero(texto(ide, "nNF"));
            nota.setSerie(texto(ide, "serie"));
            nota.setDataEmissao(extrairDataEmissao(ide));
            nota.setFornecedorCnpj(texto(emit, "CNPJ"));
            nota.setFornecedorRazaoSocial(texto(emit, "xNome"));
            nota.setValorTotalNota(decimal(icmsTot, "vNF"));
            nota.setNomeArquivoOriginal(nomeArquivo);

            NodeList detalhes = infNFe.getElementsByTagName("det");
            for (int i = 0; i < detalhes.getLength(); i++) {
                Element det = (Element) detalhes.item(i);
                nota.getItens().add(lerItem(det, i + 1));
            }

            if (nota.getItens().isEmpty()) {
                throw new XmlNfeInvalidoException("Nota sem nenhum item (tag det) -- verifique se o arquivo esta completo.");
            }
            if (nota.getChaveAcesso() == null || nota.getChaveAcesso().length() != 44) {
                throw new XmlNfeInvalidoException("Chave de acesso ausente ou invalida (esperado 44 digitos).");
            }

            return nota;
        } catch (XmlNfeInvalidoException e) {
            throw e;
        } catch (Exception e) {
            throw new XmlNfeInvalidoException("Falha ao ler o XML: " + e.getMessage());
        }
    }

    /**
     * Le uma NF-e de saida (venda) -- mesmo layout, mas usa o destinatario
     * (quem comprou) em vez do emitente, e nao precisa dos itens (essa nota
     * so alimenta o faturamento anual, nao calcula credito).
     */
    @Override
    public NotaFiscalSaida lerSaida(byte[] conteudoXml, String nomeArquivo) {
        try {
            Document documento = parseDocumentoSeguro(conteudoXml);

            Element infNFe = primeiroElemento(documento.getDocumentElement(), "infNFe");
            if (infNFe == null) {
                throw new XmlNfeInvalidoException("Nao parece ser um XML de NF-e valido (tag infNFe nao encontrada).");
            }

            Element ide = primeiroElemento(infNFe, "ide");
            Element dest = primeiroElemento(infNFe, "dest");
            Element total = primeiroElemento(infNFe, "total");
            Element icmsTot = total != null ? primeiroElemento(total, "ICMSTot") : null;

            NotaFiscalSaida nota = new NotaFiscalSaida();
            nota.setChaveAcesso(extrairChaveAcesso(infNFe));
            nota.setNumero(texto(ide, "nNF"));
            nota.setSerie(texto(ide, "serie"));
            nota.setDataEmissao(extrairDataEmissao(ide));
            nota.setDestinatarioCnpj(texto(dest, "CNPJ"));
            nota.setDestinatarioRazaoSocial(texto(dest, "xNome"));
            nota.setValorTotalNota(decimal(icmsTot, "vNF"));
            nota.setNomeArquivoOriginal(nomeArquivo);

            if (nota.getChaveAcesso() == null || nota.getChaveAcesso().length() != 44) {
                throw new XmlNfeInvalidoException("Chave de acesso ausente ou invalida (esperado 44 digitos).");
            }
            if (nota.getDataEmissao() == null) {
                throw new XmlNfeInvalidoException("Data de emissao ausente ou invalida -- nao da pra contar no faturamento anual.");
            }

            return nota;
        } catch (XmlNfeInvalidoException e) {
            throw e;
        } catch (Exception e) {
            throw new XmlNfeInvalidoException("Falha ao ler o XML: " + e.getMessage());
        }
    }

    private Document parseDocumentoSeguro(byte[] conteudoXml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // Protecao contra XXE: desabilita DTD e entidades externas.
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(conteudoXml));
    }

    private ItemNotaFiscal lerItem(Element det, int numeroItem) {
        Element prod = primeiroElemento(det, "prod");
        Element imposto = primeiroElemento(det, "imposto");

        ItemNotaFiscal item = new ItemNotaFiscal();
        item.setNumeroItem(numeroItem);
        item.setDescricaoProduto(texto(prod, "xProd"));
        item.setNcm(texto(prod, "NCM"));
        item.setCfop(texto(prod, "CFOP"));
        item.setValorProduto(decimal(prod, "vProd"));

        if (imposto != null) {
            Element grupoIcms = primeiroElemento(imposto, "ICMS");
            Element cstIcmsElemento = grupoIcms != null ? primeiroFilhoComTag(grupoIcms) : null;
            if (cstIcmsElemento != null) {
                String cst = texto(cstIcmsElemento, "CST");
                item.setCstIcms(cst != null ? cst : texto(cstIcmsElemento, "CSOSN"));
                item.setValorIcms(decimal(cstIcmsElemento, "vICMS"));
            }

            Element grupoPis = primeiroElemento(imposto, "PIS");
            Element cstPisElemento = grupoPis != null ? primeiroFilhoComTag(grupoPis) : null;
            if (cstPisElemento != null) {
                item.setCstPis(texto(cstPisElemento, "CST"));
                item.setValorPis(decimal(cstPisElemento, "vPIS"));
            }

            Element grupoCofins = primeiroElemento(imposto, "COFINS");
            Element cstCofinsElemento = grupoCofins != null ? primeiroFilhoComTag(grupoCofins) : null;
            if (cstCofinsElemento != null) {
                item.setCstCofins(texto(cstCofinsElemento, "CST"));
                item.setValorCofins(decimal(cstCofinsElemento, "vCOFINS"));
            }
        }

        return item;
    }

    /** O grupo ICMS/PIS/COFINS tem um unico filho cujo nome varia (ICMS00, ICMS10, PISAliq, PISOutr...) -- pega o primeiro. */
    private Element primeiroFilhoComTag(Element pai) {
        NodeList filhos = pai.getChildNodes();
        for (int i = 0; i < filhos.getLength(); i++) {
            Node n = filhos.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) n;
            }
        }
        return null;
    }

    private Element primeiroElemento(Element pai, String tag) {
        if (pai == null) {
            return null;
        }
        NodeList lista = pai.getElementsByTagName(tag);
        return lista.getLength() > 0 ? (Element) lista.item(0) : null;
    }

    private String texto(Element pai, String tag) {
        Element el = primeiroElemento(pai, tag);
        return el != null ? el.getTextContent().trim() : null;
    }

    private BigDecimal decimal(Element pai, String tag) {
        String valor = texto(pai, tag);
        if (valor == null || valor.isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(valor);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private String extrairChaveAcesso(Element infNFe) {
        String id = infNFe.getAttribute("Id");
        if (id == null || id.isBlank()) {
            return null;
        }
        // Formato padrao: "NFe" + 44 digitos.
        return id.replaceFirst("^NFe", "").trim();
    }

    private LocalDate extrairDataEmissao(Element ide) {
        String dhEmi = texto(ide, "dhEmi");
        if (dhEmi == null) {
            dhEmi = texto(ide, "dEmi"); // layout antigo (NF-e versao <= 2.0)
        }
        if (dhEmi == null || dhEmi.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(dhEmi.substring(0, 10), DATA_EMISSAO_FORMATO);
        } catch (Exception e) {
            return null;
        }
    }
}
