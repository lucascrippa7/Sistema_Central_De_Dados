package com.empresa.sistema.application.port.in;

import com.empresa.sistema.domain.model.Evento;
import com.empresa.sistema.domain.model.UsuarioResumo;

import java.util.List;

/**
 * Casos de uso do calendario de eventos (entregas de imposto, reunioes e
 * eventos gerais). A regra de visibilidade -- cada funcionario ve so os
 * proprios eventos, administrador ve todos -- e aplicada aqui, nao na
 * infraestrutura, para nao depender de como o Spring Security representa
 * o usuario logado.
 */
public interface EventoUseCase {

    /** Eventos do mes informado, ja filtrados pela visibilidade do usuario logado. */
    List<Evento> listarPorMes(int ano, int mes, UsuarioResumo usuarioLogado, boolean administrador);

    /** Eventos de hoje ate daqui a N dias, ja filtrados pela visibilidade do usuario logado -- usado no dashboard. */
    List<Evento> listarProximosDias(int dias, UsuarioResumo usuarioLogado, boolean administrador);

    /** Lanca AcessoNegadoException se o evento nao for do usuario logado e ele nao for administrador. */
    Evento buscarPorId(Long id, UsuarioResumo usuarioLogado, boolean administrador);

    Evento salvar(Evento evento, UsuarioResumo usuarioLogado, boolean administrador);

    void excluir(Long id, UsuarioResumo usuarioLogado, boolean administrador);

    /** Interrompe a geracao de futuras ocorrencias de uma serie recorrente (nao apaga as ja geradas). */
    void pausarSerie(Long serieId, UsuarioResumo usuarioLogado, boolean administrador);

    /** Usado pelo job agendado: garante que toda serie ativa tem ocorrencias geradas ate N meses a frente. */
    void garantirOcorrenciasProximosMeses(int mesesAFrente);
}
