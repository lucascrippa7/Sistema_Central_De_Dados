package com.empresa.sistema.application.service;

import com.empresa.sistema.application.port.in.AuditoriaUseCase;
import com.empresa.sistema.application.port.in.ClienteExcelUseCase;
import com.empresa.sistema.application.port.in.ClienteUseCase;
import com.empresa.sistema.application.port.out.GeradorPlanilhaClientesPort;
import com.empresa.sistema.application.port.out.LeitorPlanilhaClientesPort;
import com.empresa.sistema.application.port.out.LinhaClientePlanilha;
import com.empresa.sistema.application.port.out.UsuarioRepositoryPort;
import com.empresa.sistema.domain.model.AcaoAuditoria;
import com.empresa.sistema.domain.model.Cliente;
import com.empresa.sistema.domain.model.ColunaClientePlanilha;
import com.empresa.sistema.domain.model.FiltroCliente;
import com.empresa.sistema.domain.model.ResultadoImportacao;
import com.empresa.sistema.domain.model.UsuarioResumo;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class ClienteExcelApplicationService implements ClienteExcelUseCase {

    // Colunas suportadas na importacao -- CNAEs e Codigos de Servico ficam
    // de fora do "modelo" porque a importacao ainda nao cadastra essas
    // sublistas (so os dados basicos do cliente).
    private static final Set<ColunaClientePlanilha> COLUNAS_IMPORTAVEIS =
            EnumSet.complementOf(EnumSet.of(ColunaClientePlanilha.CNAES, ColunaClientePlanilha.CODIGOS_SERVICO));

    private final ClienteUseCase clienteUseCase;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final GeradorPlanilhaClientesPort geradorPlanilhaClientesPort;
    private final LeitorPlanilhaClientesPort leitorPlanilhaClientesPort;
    private final AuditoriaUseCase auditoriaUseCase;

    public ClienteExcelApplicationService(ClienteUseCase clienteUseCase,
                                           UsuarioRepositoryPort usuarioRepositoryPort,
                                           GeradorPlanilhaClientesPort geradorPlanilhaClientesPort,
                                           LeitorPlanilhaClientesPort leitorPlanilhaClientesPort,
                                           AuditoriaUseCase auditoriaUseCase) {
        this.clienteUseCase = clienteUseCase;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.geradorPlanilhaClientesPort = geradorPlanilhaClientesPort;
        this.leitorPlanilhaClientesPort = leitorPlanilhaClientesPort;
        this.auditoriaUseCase = auditoriaUseCase;
    }

    @Override
    public byte[] exportar(FiltroCliente filtro, Set<ColunaClientePlanilha> colunas) {
        List<Cliente> clientes = clienteUseCase.listar(filtro);
        Set<ColunaClientePlanilha> colunasEfetivas = (colunas == null || colunas.isEmpty())
                ? EnumSet.allOf(ColunaClientePlanilha.class)
                : colunas;
        return geradorPlanilhaClientesPort.gerar(clientes, colunasEfetivas);
    }

    @Override
    public byte[] gerarModelo() {
        return geradorPlanilhaClientesPort.gerar(Collections.emptyList(), COLUNAS_IMPORTAVEIS);
    }

    @Override
    public ResultadoImportacao importar(InputStream arquivo, UsuarioResumo usuarioLogado) {
        ResultadoImportacao resultado = new ResultadoImportacao();

        List<LinhaClientePlanilha> linhas;
        try {
            linhas = leitorPlanilhaClientesPort.ler(arquivo);
        } catch (IOException e) {
            resultado.adicionarErro("Nao foi possivel ler o arquivo. Confira se e um .xlsx valido.");
            return resultado;
        }

        resultado.setTotalLinhas(linhas.size());

        // Cada linha e salva com sua propria transacao (dentro de
        // clienteUseCase.salvar) -- de proposito, para uma linha com
        // problema nao derrubar as outras que estavam corretas.
        for (LinhaClientePlanilha linha : linhas) {
            if (linha.getRazaoSocial() == null || linha.getRazaoSocial().isBlank()) {
                resultado.adicionarErro("Linha " + linha.getNumeroLinha() + ": razao social em branco, linha ignorada");
                continue;
            }
            try {
                Cliente cliente = new Cliente();
                cliente.setRazaoSocial(linha.getRazaoSocial());
                cliente.setNomeFantasia(linha.getNomeFantasia());
                cliente.setCnpj(linha.getCnpj());
                cliente.setRegimeTributario(linha.getRegimeTributario());
                cliente.setCidade(linha.getCidade());
                cliente.setEstado(linha.getEstado());
                cliente.setTipoServico(linha.getTipoServico());
                cliente.setDescricaoServico(linha.getDescricaoServico());
                cliente.setInscricaoMunicipal(linha.getInscricaoMunicipal());
                cliente.setInscricaoEstadual(linha.getInscricaoEstadual());
                cliente.setCodigoSistemaExterno(linha.getCodigoSistemaExterno());
                cliente.setAtivo(linha.isAtivo());

                if (linha.getResponsavelLogin() != null && !linha.getResponsavelLogin().isBlank()) {
                    usuarioRepositoryPort.findByLogin(linha.getResponsavelLogin().trim())
                            .ifPresentOrElse(
                                    usuario -> cliente.setResponsavel(usuario.paraResumo()),
                                    () -> resultado.adicionarErro("Linha " + linha.getNumeroLinha() +
                                            ": responsavel '" + linha.getResponsavelLogin() + "' nao encontrado (login incorreto?), campo deixado em branco")
                            );
                }

                clienteUseCase.salvar(cliente, usuarioLogado);
                resultado.incrementarImportados();
            } catch (Exception e) {
                resultado.adicionarErro("Linha " + linha.getNumeroLinha() + ": erro ao importar - " + e.getMessage());
            }
        }

        auditoriaUseCase.registrar(usuarioLogado, "CLIENTE", null, AcaoAuditoria.CRIACAO,
                "Importacao de planilha: " + resultado.getImportados() + " de " + resultado.getTotalLinhas() + " linha(s)");

        return resultado;
    }
}
