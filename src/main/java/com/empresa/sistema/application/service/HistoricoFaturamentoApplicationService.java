package com.empresa.sistema.application.service;

import com.empresa.sistema.application.port.in.AuditoriaUseCase;
import com.empresa.sistema.application.port.in.HistoricoFaturamentoUseCase;
import com.empresa.sistema.application.port.out.HistoricoFaturamentoRepositoryPort;
import com.empresa.sistema.application.port.out.NotaFiscalSaidaRepositoryPort;
import com.empresa.sistema.domain.model.AcaoAuditoria;
import com.empresa.sistema.domain.model.HistoricoFaturamento;
import com.empresa.sistema.domain.model.HistoricoFaturamentoAno;
import com.empresa.sistema.domain.model.UsuarioResumo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class HistoricoFaturamentoApplicationService implements HistoricoFaturamentoUseCase {

    /** Quantos anos pra tras mostrar quando nao ha nenhum dado ainda (pra nao aparecer uma lista vazia sem nem os campos pra preencher). */
    private static final int ANOS_PADRAO_PARA_TRAS = 5;

    private final HistoricoFaturamentoRepositoryPort historicoFaturamentoRepositoryPort;
    private final NotaFiscalSaidaRepositoryPort notaFiscalSaidaRepositoryPort;
    private final AuditoriaUseCase auditoriaUseCase;

    public HistoricoFaturamentoApplicationService(HistoricoFaturamentoRepositoryPort historicoFaturamentoRepositoryPort,
                                                   NotaFiscalSaidaRepositoryPort notaFiscalSaidaRepositoryPort,
                                                   AuditoriaUseCase auditoriaUseCase) {
        this.historicoFaturamentoRepositoryPort = historicoFaturamentoRepositoryPort;
        this.notaFiscalSaidaRepositoryPort = notaFiscalSaidaRepositoryPort;
        this.auditoriaUseCase = auditoriaUseCase;
    }

    @Override
    public List<HistoricoFaturamentoAno> listarPorCliente(Long clienteId) {
        Map<Integer, BigDecimal> manuaisPorAno = new TreeMap<>();
        for (HistoricoFaturamento h : historicoFaturamentoRepositoryPort.findByClienteId(clienteId)) {
            manuaisPorAno.put(h.getAno(), h.getValorFaturamento());
        }

        Map<Integer, BigDecimal> calculadoPorAno = notaFiscalSaidaRepositoryPort.somarValorTotalPorAno(clienteId);
        Map<Integer, Integer> quantidadePorAno = notaFiscalSaidaRepositoryPort.contarPorAno(clienteId);

        int anoAtual = Year.now().getValue();
        int anoMaisAntigo = anoAtual - ANOS_PADRAO_PARA_TRAS;
        for (Integer ano : manuaisPorAno.keySet()) {
            anoMaisAntigo = Math.min(anoMaisAntigo, ano);
        }
        for (Integer ano : calculadoPorAno.keySet()) {
            anoMaisAntigo = Math.min(anoMaisAntigo, ano);
        }

        List<HistoricoFaturamentoAno> resultado = new java.util.ArrayList<>();
        for (int ano = anoAtual; ano >= anoMaisAntigo; ano--) {
            HistoricoFaturamentoAno item = new HistoricoFaturamentoAno();
            item.setAno(ano);
            item.setValorManual(manuaisPorAno.get(ano));
            item.setValorCalculadoNfe(calculadoPorAno.getOrDefault(ano, BigDecimal.ZERO));
            item.setQuantidadeNotasSaida(quantidadePorAno.getOrDefault(ano, 0));
            resultado.add(item);
        }
        return resultado;
    }

    @Transactional
    @Override
    public void definirManual(Long clienteId, int ano, BigDecimal valor, UsuarioResumo usuarioLogado) {
        if (valor == null) {
            historicoFaturamentoRepositoryPort.deleteByClienteIdAndAno(clienteId, ano);
            auditoriaUseCase.registrar(usuarioLogado, "CLIENTE", clienteId, AcaoAuditoria.EDICAO,
                    "Faturamento manual de " + ano + " removido (volta a valer o calculado via NF-e, se houver)");
            return;
        }

        HistoricoFaturamento historico = historicoFaturamentoRepositoryPort.findByClienteIdAndAno(clienteId, ano)
                .orElseGet(HistoricoFaturamento::new);
        historico.setClienteId(clienteId);
        historico.setAno(ano);
        historico.setValorFaturamento(valor);
        historico.setAtualizadoEm(LocalDateTime.now());
        historico.setAtualizadoPor(usuarioLogado);
        historicoFaturamentoRepositoryPort.save(historico);

        auditoriaUseCase.registrar(usuarioLogado, "CLIENTE", clienteId, AcaoAuditoria.EDICAO,
                "Faturamento de " + ano + " informado manualmente: " + valor);
    }
}
