package com.empresa.sistema.application.service;

import com.empresa.sistema.application.port.in.AuditoriaUseCase;
import com.empresa.sistema.application.port.out.EventoRepositoryPort;
import com.empresa.sistema.domain.exception.AcessoNegadoException;
import com.empresa.sistema.domain.exception.ClienteObrigatorioException;
import com.empresa.sistema.domain.exception.EventoNaoEncontradoException;
import com.empresa.sistema.domain.model.Evento;
import com.empresa.sistema.domain.model.TipoEvento;
import com.empresa.sistema.domain.model.UsuarioResumo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes da regra de negocio de Evento -- sem subir Spring nem banco, so
 * mockando as portas de saida (EventoRepositoryPort, AuditoriaUseCase).
 * Foco nos pontos mais arriscados: quem pode ver/editar/excluir o evento de
 * quem, e a obrigatoriedade de cliente pra entregas de imposto.
 */
@ExtendWith(MockitoExtension.class)
class EventoApplicationServiceTest {

    @Mock
    private EventoRepositoryPort eventoRepositoryPort;

    @Mock
    private AuditoriaUseCase auditoriaUseCase;

    private EventoApplicationService service;

    private UsuarioResumo lucas;
    private UsuarioResumo maria;

    @BeforeEach
    void configurar() {
        service = new EventoApplicationService(eventoRepositoryPort, auditoriaUseCase);
        lucas = new UsuarioResumo(1L, "Lucas", "lucas");
        maria = new UsuarioResumo(2L, "Maria", "maria");
    }

    @Test
    void naoDeveSalvarEntregaDeImpostoSemCliente() {
        Evento evento = new Evento();
        evento.setTipo(TipoEvento.ENTREGA_IMPOSTO);
        evento.setClienteId(null);

        assertThatThrownBy(() -> service.salvar(evento, lucas, false))
                .isInstanceOf(ClienteObrigatorioException.class);

        verifyNoInteractions(eventoRepositoryPort);
    }

    @Test
    void devePermitirEntregaDeImpostoComCliente() {
        Evento evento = eventoNovoValido();
        evento.setTipo(TipoEvento.ENTREGA_IMPOSTO);
        evento.setClienteId(99L);
        when(eventoRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Evento salvo = service.salvar(evento, lucas, false);

        assertThat(salvo.getClienteId()).isEqualTo(99L);
    }

    @Test
    void quemCriaOEventoViraODono() {
        Evento evento = eventoNovoValido();
        when(eventoRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Evento salvo = service.salvar(evento, lucas, false);

        assertThat(salvo.getUsuario()).isEqualTo(lucas);
        assertThat(salvo.getCriadoPor()).isEqualTo(lucas);
    }

    @Test
    void naoDonoNaoAdministradorNaoDeveVerEventoDeOutroFuncionario() {
        Evento eventoDaMaria = eventoExistenteDe(maria);
        when(eventoRepositoryPort.findById(10L)).thenReturn(Optional.of(eventoDaMaria));

        assertThatThrownBy(() -> service.buscarPorId(10L, lucas, false))
                .isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    void administradorDeveVerEventoDeQualquerFuncionario() {
        Evento eventoDaMaria = eventoExistenteDe(maria);
        when(eventoRepositoryPort.findById(10L)).thenReturn(Optional.of(eventoDaMaria));

        Evento resultado = service.buscarPorId(10L, lucas, true);

        assertThat(resultado).isEqualTo(eventoDaMaria);
    }

    @Test
    void donoDeveConseguirVerOProprioEvento() {
        Evento eventoDoLucas = eventoExistenteDe(lucas);
        when(eventoRepositoryPort.findById(10L)).thenReturn(Optional.of(eventoDoLucas));

        Evento resultado = service.buscarPorId(10L, lucas, false);

        assertThat(resultado).isEqualTo(eventoDoLucas);
    }

    @Test
    void naoDeveExcluirEventoNaoEncontrado() {
        when(eventoRepositoryPort.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.excluir(999L, lucas, false))
                .isInstanceOf(EventoNaoEncontradoException.class);

        verify(eventoRepositoryPort, never()).deleteById(any());
    }

    @Test
    void naoDeveExcluirEventoDeOutroFuncionario() {
        Evento eventoDaMaria = eventoExistenteDe(maria);
        when(eventoRepositoryPort.findById(10L)).thenReturn(Optional.of(eventoDaMaria));

        assertThatThrownBy(() -> service.excluir(10L, lucas, false))
                .isInstanceOf(AcessoNegadoException.class);

        verify(eventoRepositoryPort, never()).deleteById(any());
    }

    @Test
    void listarPorMesDeveGerarOcorrenciaQuandoSerieAtivaNaoTemNoMesAinda() {
        Evento mestre = eventoExistenteDe(lucas);
        mestre.setId(5L);
        mestre.setSerieId(5L);
        mestre.setRecorrente(true);
        mestre.setSerieAtiva(true);
        mestre.setDiaMes(31); // dia que nem todo mes tem -- testa o "clamping"
        mestre.setSerieAnoReferencia(2026);
        mestre.setSerieMesReferencia(1);
        mestre.setLembretes(List.of());

        when(eventoRepositoryPort.findMestresDeSeriesAtivas()).thenReturn(List.of(mestre));
        when(eventoRepositoryPort.existeOcorrenciaParaMes(5L, 2026, 2)).thenReturn(false);
        when(eventoRepositoryPort.findByPeriodo(any(), any(), any())).thenReturn(List.of());

        service.listarPorMes(2026, 2, lucas, false);

        ArgumentCaptor<Evento> captor = ArgumentCaptor.forClass(Evento.class);
        verify(eventoRepositoryPort).save(captor.capture());
        // Fevereiro/2026 nao tem dia 31 -- tem que cair no ultimo dia do mes (28).
        assertThat(captor.getValue().getData()).isEqualTo(LocalDate.of(2026, 2, 28));
    }

    @Test
    void listarPorMesNaoDeveGerarOcorrenciaDuplicadaSeJaExiste() {
        Evento mestre = eventoExistenteDe(lucas);
        mestre.setId(5L);
        mestre.setSerieId(5L);
        mestre.setRecorrente(true);
        mestre.setSerieAtiva(true);
        mestre.setDiaMes(10);
        mestre.setSerieAnoReferencia(2026);
        mestre.setSerieMesReferencia(1);

        when(eventoRepositoryPort.findMestresDeSeriesAtivas()).thenReturn(List.of(mestre));
        when(eventoRepositoryPort.existeOcorrenciaParaMes(5L, 2026, 2)).thenReturn(true);
        when(eventoRepositoryPort.findByPeriodo(any(), any(), any())).thenReturn(List.of());

        service.listarPorMes(2026, 2, lucas, false);

        verify(eventoRepositoryPort, never()).save(any());
    }

    private Evento eventoNovoValido() {
        Evento evento = new Evento();
        evento.setTitulo("Reuniao");
        evento.setTipo(TipoEvento.REUNIAO);
        evento.setData(LocalDate.of(2026, 3, 10));
        return evento;
    }

    private Evento eventoExistenteDe(UsuarioResumo dono) {
        Evento evento = new Evento();
        evento.setId(10L);
        evento.setTitulo("Reuniao");
        evento.setTipo(TipoEvento.REUNIAO);
        evento.setData(LocalDate.of(2026, 3, 10));
        evento.setUsuario(dono);
        return evento;
    }
}
