package com.empresa.sistema.application.service;

import com.empresa.sistema.application.port.in.AuditoriaUseCase;
import com.empresa.sistema.application.port.in.EventoUseCase;
import com.empresa.sistema.application.port.out.EventoRepositoryPort;
import com.empresa.sistema.domain.exception.AcessoNegadoException;
import com.empresa.sistema.domain.exception.ClienteObrigatorioException;
import com.empresa.sistema.domain.exception.EventoNaoEncontradoException;
import com.empresa.sistema.domain.model.AcaoAuditoria;
import com.empresa.sistema.domain.model.Evento;
import com.empresa.sistema.domain.model.TipoEvento;
import com.empresa.sistema.domain.model.UsuarioResumo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventoApplicationService implements EventoUseCase {

    /** Quantos meses pra frente pre-gerar de uma vez quando uma serie e criada/verificada. */
    private static final int MESES_PRE_GERADOS = 11;

    private final EventoRepositoryPort eventoRepositoryPort;
    private final AuditoriaUseCase auditoriaUseCase;

    public EventoApplicationService(EventoRepositoryPort eventoRepositoryPort, AuditoriaUseCase auditoriaUseCase) {
        this.eventoRepositoryPort = eventoRepositoryPort;
        this.auditoriaUseCase = auditoriaUseCase;
    }

    @Transactional
    @Override
    public List<Evento> listarPorMes(int ano, int mes, UsuarioResumo usuarioLogado, boolean administrador) {
        // Antes de listar, garante que toda serie recorrente ativa ja tem a
        // ocorrencia deste mes gerada -- isso vale pra qualquer mes que
        // alguem navegue no calendario, independente de quem esta olhando
        // (o dono da serie pode nao ser quem esta navegando agora).
        for (Evento mestre : eventoRepositoryPort.findMestresDeSeriesAtivas()) {
            garantirOcorrenciaParaMes(mestre, ano, mes);
        }

        YearMonth mesConsultado = YearMonth.of(ano, mes);
        LocalDate inicio = mesConsultado.atDay(1);
        LocalDate fim = mesConsultado.atEndOfMonth();

        Long usuarioId = administrador ? null : usuarioLogado.getId();
        return eventoRepositoryPort.findByPeriodo(inicio, fim, usuarioId);
    }

    @Override
    public List<Evento> listarProximosDias(int dias, UsuarioResumo usuarioLogado, boolean administrador) {
        // Nao precisa garantir ocorrencias aqui: o NotificacaoScheduler ja
        // roda diariamente garantindo que as series ativas tenham ocorrencias
        // geradas pros proximos meses -- isso e so consulta.
        LocalDate hoje = LocalDate.now();
        Long usuarioId = administrador ? null : usuarioLogado.getId();
        return eventoRepositoryPort.findByPeriodo(hoje, hoje.plusDays(dias), usuarioId);
    }

    @Override
    public Evento buscarPorId(Long id, UsuarioResumo usuarioLogado, boolean administrador) {
        Evento evento = eventoRepositoryPort.findById(id)
                .orElseThrow(() -> new EventoNaoEncontradoException(id));

        verificarPropriedade(evento, usuarioLogado, administrador);
        return evento;
    }

    @Transactional
    @Override
    public Evento salvar(Evento evento, UsuarioResumo usuarioLogado, boolean administrador) {
        if (evento.getTipo() == TipoEvento.ENTREGA_IMPOSTO && evento.getClienteId() == null) {
            throw new ClienteObrigatorioException();
        }

        boolean novo = (evento.getId() == null);

        if (!novo) {
            // O formulario so manda os campos basicos -- preserva o dono do
            // evento, quando foi criado, e a configuracao de serie (que so
            // e alterada via pausarSerie, nunca pelo formulario normal).
            Evento existente = eventoRepositoryPort.findById(evento.getId())
                    .orElseThrow(() -> new EventoNaoEncontradoException(evento.getId()));
            verificarPropriedade(existente, usuarioLogado, administrador);

            evento.setUsuario(existente.getUsuario());
            evento.setCriadoPor(existente.getCriadoPor());
            evento.setCriadoEm(existente.getCriadoEm());
            evento.setRecorrente(existente.isRecorrente());
            evento.setSerieId(existente.getSerieId());
            evento.setSerieAtiva(existente.isSerieAtiva());
            evento.setDiaMes(existente.getDiaMes());
            evento.setSerieAnoReferencia(existente.getSerieAnoReferencia());
            evento.setSerieMesReferencia(existente.getSerieMesReferencia());
        } else {
            // O dono do evento e sempre quem criou -- inclusive quando e o
            // administrador criando o proprio evento.
            evento.setUsuario(usuarioLogado);
            evento.setCriadoPor(usuarioLogado);
            evento.setCriadoEm(LocalDateTime.now());

            if (evento.isRecorrente()) {
                evento.setSerieAtiva(true);
                evento.setDiaMes(evento.getData().getDayOfMonth());
                evento.setSerieAnoReferencia(evento.getData().getYear());
                evento.setSerieMesReferencia(evento.getData().getMonthValue());
            }
        }

        evento.setAtualizadoPor(usuarioLogado);
        evento.setAtualizadoEm(LocalDateTime.now());

        Evento salvo = eventoRepositoryPort.save(evento);

        if (novo && salvo.isRecorrente()) {
            // O primeiro evento da serie aponta pra si mesmo -- e assim que
            // o "mestre" e identificado (id == serieId).
            salvo.setSerieId(salvo.getId());
            salvo = eventoRepositoryPort.save(salvo);
            gerarOcorrenciasFuturas(salvo, MESES_PRE_GERADOS);
        }

        auditoriaUseCase.registrar(usuarioLogado, "EVENTO", salvo.getId(),
                novo ? AcaoAuditoria.CRIACAO : AcaoAuditoria.EDICAO,
                "Evento: " + salvo.getTitulo());

        return salvo;
    }

    @Transactional
    @Override
    public void excluir(Long id, UsuarioResumo usuarioLogado, boolean administrador) {
        Evento evento = eventoRepositoryPort.findById(id)
                .orElseThrow(() -> new EventoNaoEncontradoException(id));
        verificarPropriedade(evento, usuarioLogado, administrador);

        eventoRepositoryPort.deleteById(id);

        auditoriaUseCase.registrar(usuarioLogado, "EVENTO", id, AcaoAuditoria.EXCLUSAO,
                "Evento: " + evento.getTitulo());
    }

    @Transactional
    @Override
    public void pausarSerie(Long serieId, UsuarioResumo usuarioLogado, boolean administrador) {
        Evento mestre = eventoRepositoryPort.findById(serieId)
                .orElseThrow(() -> new EventoNaoEncontradoException(serieId));
        verificarPropriedade(mestre, usuarioLogado, administrador);

        mestre.setSerieAtiva(false);
        eventoRepositoryPort.save(mestre);

        auditoriaUseCase.registrar(usuarioLogado, "EVENTO", serieId, AcaoAuditoria.EDICAO,
                "Recorrencia interrompida: " + mestre.getTitulo());
    }

    @Transactional
    @Override
    public void garantirOcorrenciasProximosMeses(int mesesAFrente) {
        for (Evento mestre : eventoRepositoryPort.findMestresDeSeriesAtivas()) {
            gerarOcorrenciasFuturas(mestre, mesesAFrente);
        }
    }

    private void gerarOcorrenciasFuturas(Evento mestre, int mesesAFrente) {
        YearMonth atual = YearMonth.now();
        for (int i = 0; i <= mesesAFrente; i++) {
            YearMonth alvo = atual.plusMonths(i);
            garantirOcorrenciaParaMes(mestre, alvo.getYear(), alvo.getMonthValue());
        }
    }

    /**
     * Garante que a serie do mestre informado tem uma ocorrencia gerada
     * para o ano/mes pedido -- se ja existir (inclusive o proprio mestre,
     * que representa o mes em que a serie comecou), nao faz nada.
     */
    private void garantirOcorrenciaParaMes(Evento mestre, int ano, int mes) {
        if (eventoRepositoryPort.existeOcorrenciaParaMes(mestre.getSerieId(), ano, mes)) return;

        YearMonth alvo = YearMonth.of(ano, mes);
        YearMonth inicioDaSerie = YearMonth.of(mestre.getSerieAnoReferencia(), mestre.getSerieMesReferencia());
        if (alvo.isBefore(inicioDaSerie)) return;

        int dia = Math.min(mestre.getDiaMes(), alvo.lengthOfMonth());

        Evento ocorrencia = new Evento();
        ocorrencia.setTitulo(mestre.getTitulo());
        ocorrencia.setDescricao(mestre.getDescricao());
        ocorrencia.setTipo(mestre.getTipo());
        ocorrencia.setData(alvo.atDay(dia));
        ocorrencia.setHorario(mestre.getHorario());
        ocorrencia.setClienteId(mestre.getClienteId());
        ocorrencia.setUsuario(mestre.getUsuario());
        ocorrencia.setRecorrente(true);
        ocorrencia.setSerieId(mestre.getSerieId());
        ocorrencia.setSerieAtiva(mestre.isSerieAtiva());
        ocorrencia.setDiaMes(mestre.getDiaMes());
        ocorrencia.setSerieAnoReferencia(ano);
        ocorrencia.setSerieMesReferencia(mes);
        ocorrencia.setLembretes(new ArrayList<>(mestre.getLembretes()));
        ocorrencia.setCriadoPor(mestre.getCriadoPor());
        ocorrencia.setCriadoEm(LocalDateTime.now());
        ocorrencia.setAtualizadoPor(mestre.getCriadoPor());
        ocorrencia.setAtualizadoEm(LocalDateTime.now());

        eventoRepositoryPort.save(ocorrencia);
    }

    private void verificarPropriedade(Evento evento, UsuarioResumo usuarioLogado, boolean administrador) {
        if (administrador) return;
        if (evento.getUsuario() == null || !evento.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AcessoNegadoException("Este evento pertence a outro funcionario");
        }
    }
}
