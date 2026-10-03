package com.empresa.sistema.infrastructure.excel;

import com.empresa.sistema.application.port.out.GeradorPlanilhaUsuariosPort;
import com.empresa.sistema.domain.model.ColunaUsuarioPlanilha;
import com.empresa.sistema.domain.model.Usuario;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GeradorPlanilhaUsuariosAdapter implements GeradorPlanilhaUsuariosPort {

    private static final String COLUNA_NOME = "Nome";

    @Override
    public byte[] gerar(List<Usuario> usuarios, Set<ColunaUsuarioPlanilha> colunas) {
        List<ColunaUsuarioPlanilha> colunasOrdenadas = Arrays.stream(ColunaUsuarioPlanilha.values())
                .filter(colunas::contains)
                .collect(Collectors.toList());

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream saida = new ByteArrayOutputStream()) {
            Sheet planilha = workbook.createSheet("Usuarios");

            CellStyle estiloCabecalho = workbook.createCellStyle();
            Font fonteNegrito = workbook.createFont();
            fonteNegrito.setBold(true);
            estiloCabecalho.setFont(fonteNegrito);

            Row linhaCabecalho = planilha.createRow(0);
            Cell primeiraCelula = linhaCabecalho.createCell(0);
            primeiraCelula.setCellValue(COLUNA_NOME);
            primeiraCelula.setCellStyle(estiloCabecalho);

            for (int i = 0; i < colunasOrdenadas.size(); i++) {
                Cell celula = linhaCabecalho.createCell(i + 1);
                celula.setCellValue(colunasOrdenadas.get(i).getRotulo());
                celula.setCellStyle(estiloCabecalho);
            }

            int numeroLinha = 1;
            for (Usuario usuario : usuarios) {
                Row linha = planilha.createRow(numeroLinha++);
                linha.createCell(0).setCellValue(nvl(usuario.getNome()));

                for (int i = 0; i < colunasOrdenadas.size(); i++) {
                    linha.createCell(i + 1).setCellValue(valorDaColuna(usuario, colunasOrdenadas.get(i)));
                }
            }

            for (int i = 0; i <= colunasOrdenadas.size(); i++) {
                planilha.autoSizeColumn(i);
            }

            workbook.write(saida);
            return saida.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao gerar planilha de usuarios", e);
        }
    }

    private String valorDaColuna(Usuario usuario, ColunaUsuarioPlanilha coluna) {
        return switch (coluna) {
            case LOGIN -> nvl(usuario.getLogin());
            case CARGO -> nvl(usuario.getCargo());
            case TIPO_ACESSO -> usuario.getTipoAcesso() != null ? usuario.getTipoAcesso().name() : "";
            case STATUS -> usuario.isAtivo() ? "Sim" : "Nao";
        };
    }

    private String nvl(String valor) {
        return valor != null ? valor : "";
    }
}
