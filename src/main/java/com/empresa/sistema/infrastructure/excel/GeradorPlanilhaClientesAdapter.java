package com.empresa.sistema.infrastructure.excel;

import com.empresa.sistema.application.port.out.GeradorPlanilhaClientesPort;
import com.empresa.sistema.domain.model.Cliente;
import com.empresa.sistema.domain.model.ColunaClientePlanilha;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Unico lugar do sistema que sabe que existe Apache POI para GERAR
 * planilhas de clientes. A ordem das colunas na planilha segue sempre a
 * ordem do enum ColunaClientePlanilha, mesmo que o usuario tenha marcado
 * as caixinhas em outra ordem na tela.
 */
@Component
public class GeradorPlanilhaClientesAdapter implements GeradorPlanilhaClientesPort {

    private static final String COLUNA_RAZAO_SOCIAL = "Razao Social";

    @Override
    public byte[] gerar(List<Cliente> clientes, Set<ColunaClientePlanilha> colunas) {
        List<ColunaClientePlanilha> colunasOrdenadas = Arrays.stream(ColunaClientePlanilha.values())
                .filter(colunas::contains)
                .collect(Collectors.toList());

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream saida = new ByteArrayOutputStream()) {
            Sheet planilha = workbook.createSheet("Clientes");

            CellStyle estiloCabecalho = workbook.createCellStyle();
            Font fonteNegrito = workbook.createFont();
            fonteNegrito.setBold(true);
            estiloCabecalho.setFont(fonteNegrito);

            Row linhaCabecalho = planilha.createRow(0);
            Cell primeiraCelula = linhaCabecalho.createCell(0);
            primeiraCelula.setCellValue(COLUNA_RAZAO_SOCIAL);
            primeiraCelula.setCellStyle(estiloCabecalho);

            for (int i = 0; i < colunasOrdenadas.size(); i++) {
                Cell celula = linhaCabecalho.createCell(i + 1);
                celula.setCellValue(colunasOrdenadas.get(i).getRotulo());
                celula.setCellStyle(estiloCabecalho);
            }

            int numeroLinha = 1;
            for (Cliente cliente : clientes) {
                Row linha = planilha.createRow(numeroLinha++);
                linha.createCell(0).setCellValue(nvl(cliente.getRazaoSocial()));

                for (int i = 0; i < colunasOrdenadas.size(); i++) {
                    linha.createCell(i + 1).setCellValue(valorDaColuna(cliente, colunasOrdenadas.get(i)));
                }
            }

            for (int i = 0; i <= colunasOrdenadas.size(); i++) {
                planilha.autoSizeColumn(i);
            }

            workbook.write(saida);
            return saida.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao gerar planilha de clientes", e);
        }
    }

    private String valorDaColuna(Cliente cliente, ColunaClientePlanilha coluna) {
        return switch (coluna) {
            case CNPJ -> nvl(cliente.getCnpj());
            case NOME_FANTASIA -> nvl(cliente.getNomeFantasia());
            case REGIME_TRIBUTARIO -> nvl(cliente.getRegimeTributario());
            case CIDADE -> nvl(cliente.getCidade());
            case ESTADO -> nvl(cliente.getEstado());
            case TIPO_SERVICO -> nvl(cliente.getTipoServico());
            case DESCRICAO_SERVICO -> nvl(cliente.getDescricaoServico());
            case INSCRICAO_MUNICIPAL -> nvl(cliente.getInscricaoMunicipal());
            case INSCRICAO_ESTADUAL -> nvl(cliente.getInscricaoEstadual());
            case CODIGO_SISTEMA_EXTERNO -> nvl(cliente.getCodigoSistemaExterno());
            case RESPONSAVEL -> cliente.getResponsavel() != null ? nvl(cliente.getResponsavel().getLogin()) : "";
            case STATUS -> cliente.isAtivo() ? "Sim" : "Nao";
            case CNAES -> cliente.getCnaes().stream()
                    .map(cnae -> cnae.getCodigo())
                    .collect(Collectors.joining(", "));
            case CODIGOS_SERVICO -> cliente.getCodigosServico().stream()
                    .map(codigo -> codigo.getCodigo())
                    .collect(Collectors.joining(", "));
        };
    }

    private String nvl(String valor) {
        return valor != null ? valor : "";
    }
}
