package com.empresa.sistema.infrastructure.excel;

import com.empresa.sistema.application.port.out.LeitorPlanilhaClientesPort;
import com.empresa.sistema.application.port.out.LinhaClientePlanilha;
import com.empresa.sistema.domain.model.ColunaClientePlanilha;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unico lugar do sistema que sabe que existe Apache POI para LER
 * planilhas de importacao.
 *
 * Le a linha de CABECALHO primeiro e descobre em qual coluna cada campo
 * esta, pelo NOME (nao pela posicao). Isso e de proposito: como agora e
 * possivel exportar uma planilha so com algumas colunas escolhidas, uma
 * leitura por posicao fixa quebraria (leria o dado errado na coluna
 * errada). Lendo por nome, uma planilha exportada com menos colunas (ou
 * em outra ordem) ainda e importada corretamente -- as colunas que nao
 * vieram simplesmente ficam em branco.
 */
@Component
public class LeitorPlanilhaClientesAdapter implements LeitorPlanilhaClientesPort {

    private static final String COLUNA_RAZAO_SOCIAL = "Razao Social";

    private final DataFormatter formatador = new DataFormatter();

    @Override
    public List<LinhaClientePlanilha> ler(InputStream arquivo) throws IOException {
        List<LinhaClientePlanilha> linhas = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(arquivo)) {
            Sheet planilha = workbook.getSheetAt(0);

            Row cabecalho = planilha.getRow(0);
            if (cabecalho == null) {
                return linhas;
            }
            Map<String, Integer> indicePorColuna = mapearIndicesDoCabecalho(cabecalho);

            for (int i = 1; i <= planilha.getLastRowNum(); i++) {
                Row linha = planilha.getRow(i);
                String razaoSocial = valor(linha, indicePorColuna, COLUNA_RAZAO_SOCIAL);
                if (linha == null || razaoSocial == null) {
                    continue; // linha vazia ou sem razao social, ignora
                }

                LinhaClientePlanilha item = new LinhaClientePlanilha();
                item.setNumeroLinha(i + 1);
                item.setRazaoSocial(razaoSocial);
                item.setNomeFantasia(valor(linha, indicePorColuna, ColunaClientePlanilha.NOME_FANTASIA.getRotulo()));
                item.setCnpj(valor(linha, indicePorColuna, ColunaClientePlanilha.CNPJ.getRotulo()));
                item.setRegimeTributario(valor(linha, indicePorColuna, ColunaClientePlanilha.REGIME_TRIBUTARIO.getRotulo()));
                item.setCidade(valor(linha, indicePorColuna, ColunaClientePlanilha.CIDADE.getRotulo()));
                item.setEstado(valor(linha, indicePorColuna, ColunaClientePlanilha.ESTADO.getRotulo()));
                item.setTipoServico(valor(linha, indicePorColuna, ColunaClientePlanilha.TIPO_SERVICO.getRotulo()));
                item.setDescricaoServico(valor(linha, indicePorColuna, ColunaClientePlanilha.DESCRICAO_SERVICO.getRotulo()));
                item.setInscricaoMunicipal(valor(linha, indicePorColuna, ColunaClientePlanilha.INSCRICAO_MUNICIPAL.getRotulo()));
                item.setInscricaoEstadual(valor(linha, indicePorColuna, ColunaClientePlanilha.INSCRICAO_ESTADUAL.getRotulo()));
                item.setCodigoSistemaExterno(valor(linha, indicePorColuna, ColunaClientePlanilha.CODIGO_SISTEMA_EXTERNO.getRotulo()));
                item.setResponsavelLogin(valor(linha, indicePorColuna, ColunaClientePlanilha.RESPONSAVEL.getRotulo()));

                String ativoTexto = valor(linha, indicePorColuna, ColunaClientePlanilha.STATUS.getRotulo());
                item.setAtivo(ativoTexto == null
                        || ativoTexto.equalsIgnoreCase("sim")
                        || ativoTexto.equalsIgnoreCase("s"));

                linhas.add(item);
            }
        }

        return linhas;
    }

    private Map<String, Integer> mapearIndicesDoCabecalho(Row cabecalho) {
        Map<String, Integer> indices = new HashMap<>();
        for (Cell celula : cabecalho) {
            String nomeColuna = formatador.formatCellValue(celula).trim();
            if (!nomeColuna.isBlank()) {
                indices.put(nomeColuna, celula.getColumnIndex());
            }
        }
        return indices;
    }

    private String valor(Row linha, Map<String, Integer> indicePorColuna, String nomeColuna) {
        if (linha == null) {
            return null;
        }
        Integer indice = indicePorColuna.get(nomeColuna);
        if (indice == null) {
            return null; // essa coluna nao veio na planilha -- tudo bem, fica em branco
        }
        Cell celula = linha.getCell(indice);
        if (celula == null) {
            return null;
        }
        String texto = formatador.formatCellValue(celula);
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
