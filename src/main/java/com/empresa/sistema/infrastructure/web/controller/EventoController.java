package com.empresa.sistema.infrastructure.web.controller;

import com.empresa.sistema.application.port.in.ClienteUseCase;
import com.empresa.sistema.application.port.in.EventoUseCase;
import com.empresa.sistema.domain.exception.AcessoNegadoException;
import com.empresa.sistema.domain.exception.ClienteObrigatorioException;
import com.empresa.sistema.domain.exception.EventoNaoEncontradoException;
import com.empresa.sistema.domain.model.Evento;
import com.empresa.sistema.domain.model.FiltroCliente;
import com.empresa.sistema.domain.model.TipoAcesso;
import com.empresa.sistema.infrastructure.security.UsuarioAutenticado;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller do calendario: grade mensal (estilo Google Calendar) com
 * entregas de imposto, reunioes e eventos, incluindo series recorrentes
 * mensais e lembretes de notificacao. So conhece EventoUseCase e
 * ClienteUseCase (para o combo de vinculo) -- nenhuma logica de negocio
 * mora aqui, so montagem da grade e conversao de campos de formulario.
 */
@Controller
@RequestMapping("/calendario")
public class EventoController {

    // Sem acentos de proposito, seguindo a convencao do restante do projeto.
    private static final String[] NOMES_MES = {
            "Janeiro", "Fevereiro", "Marco", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    };

    private final EventoUseCase eventoUseCase;
    private final ClienteUseCase clienteUseCase;

    public EventoController(EventoUseCase eventoUseCase, ClienteUseCase clienteUseCase) {
        this.eventoUseCase = eventoUseCase;
        this.clienteUseCase = clienteUseCase;
    }

    @GetMapping
    public String mes(@RequestParam(required = false) Integer ano,
                       @RequestParam(required = false) Integer mes,
                       @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
                       Model model) {
        YearMonth mesAtual = (ano != null && mes != null) ? YearMonth.of(ano, mes) : YearMonth.now();
        boolean administrador = administrador(usuarioAutenticado);

        List<Evento> eventos = eventoUseCase.listarPorMes(
                mesAtual.getYear(), mesAtual.getMonthValue(),
                usuarioAutenticado.getUsuarioResumo(), administrador);

        Map<LocalDate, List<Evento>> eventosPorDia = eventos.stream()
                .collect(Collectors.groupingBy(Evento::getData));

        model.addAttribute("semanas", montarSemanas(mesAtual));
        model.addAttribute("eventosPorDia", eventosPorDia);
        model.addAttribute("mesAtual", mesAtual);
        model.addAttribute("nomeMes", NOMES_MES[mesAtual.getMonthValue() - 1]);
        model.addAttribute("mesAnterior", mesAtual.minusMonths(1));
        model.addAttribute("proximoMes", mesAtual.plusMonths(1));
        model.addAttribute("hoje", LocalDate.now());
        model.addAttribute("administrador", administrador);
        return "calendario/mes";
    }

    @GetMapping("/novo")
    public String novoForm(@RequestParam(required = false) String data, Model model) {
        Evento evento = new Evento();
        evento.setData(data != null ? LocalDate.parse(data) : LocalDate.now());
        model.addAttribute("evento", evento);
        model.addAttribute("modoEdicao", false);
        model.addAttribute("lembretesTexto", "");
        model.addAttribute("clientes", clienteUseCase.listar(new FiltroCliente(null, null, null, true, null, null)));
        return "calendario/form";
    }

    @PostMapping("/novo")
    public String criar(@Valid @ModelAttribute("evento") Evento evento,
                         BindingResult resultado,
                         @RequestParam(required = false) String lembretesTexto,
                         @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
                         Model model) {
        evento.setLembretes(parseLembretes(lembretesTexto));

        if (resultado.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            model.addAttribute("lembretesTexto", lembretesTexto);
            model.addAttribute("clientes", clienteUseCase.listar(new FiltroCliente(null, null, null, true, null, null)));
            return "calendario/form";
        }
        try {
            Evento salvo = eventoUseCase.salvar(evento, usuarioAutenticado.getUsuarioResumo(), administrador(usuarioAutenticado));
            return "redirect:/calendario?ano=" + salvo.getData().getYear() + "&mes=" + salvo.getData().getMonthValue();
        } catch (ClienteObrigatorioException e) {
            model.addAttribute("modoEdicao", false);
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("lembretesTexto", lembretesTexto);
            model.addAttribute("clientes", clienteUseCase.listar(new FiltroCliente(null, null, null, true, null, null)));
            return "calendario/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id,
                              @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        boolean administrador = administrador(usuarioAutenticado);
        Evento evento;
        try {
            evento = eventoUseCase.buscarPorId(id, usuarioAutenticado.getUsuarioResumo(), administrador);
        } catch (EventoNaoEncontradoException e) {
            redirectAttributes.addFlashAttribute("erroCalendario", "Esse evento nao existe mais (pode ter sido excluido).");
            return "redirect:/calendario";
        } catch (AcessoNegadoException e) {
            redirectAttributes.addFlashAttribute("erroCalendario", "Esse evento e de outro funcionario -- voce nao tem acesso a ele.");
            return "redirect:/calendario";
        }
        model.addAttribute("evento", evento);
        model.addAttribute("modoEdicao", true);
        model.addAttribute("lembretesTexto", formatarLembretes(evento.getLembretes()));
        model.addAttribute("clientes", clienteUseCase.listar(new FiltroCliente(null, null, null, true, null, null)));

        if (evento.getSerieId() != null) {
            // O status/config validos da serie moram no evento "mestre" (id == serieId) --
            // se estamos editando uma ocorrencia gerada, buscamos o mestre pra mostrar
            // o status certo (a ocorrencia gerada pode estar desatualizada nesse campo).
            // Se o mestre tiver sido excluido (nao deveria, mas por seguranca), ignora
            // silenciosamente em vez de derrubar a tela -- so nao mostra o aviso de serie.
            try {
                Evento mestre = evento.getId().equals(evento.getSerieId())
                        ? evento
                        : eventoUseCase.buscarPorId(evento.getSerieId(), usuarioAutenticado.getUsuarioResumo(), administrador);
                model.addAttribute("serieAtivaAtual", mestre.isSerieAtiva());
                model.addAttribute("diaMesSerie", mestre.getDiaMes());
            } catch (EventoNaoEncontradoException | AcessoNegadoException ignorado) {
                // segue sem os dados da serie -- o formulario ja trata a ausencia deles
            }
        }
        return "calendario/form";
    }

    @PostMapping("/{id}/editar")
    public String editar(@PathVariable Long id,
                          @Valid @ModelAttribute("evento") Evento evento,
                          BindingResult resultado,
                          @RequestParam(required = false) String lembretesTexto,
                          @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
                          Model model) {
        evento.setLembretes(parseLembretes(lembretesTexto));

        if (resultado.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("lembretesTexto", lembretesTexto);
            model.addAttribute("clientes", clienteUseCase.listar(new FiltroCliente(null, null, null, true, null, null)));
            return "calendario/form";
        }
        evento.setId(id);
        try {
            Evento salvo = eventoUseCase.salvar(evento, usuarioAutenticado.getUsuarioResumo(), administrador(usuarioAutenticado));
            return "redirect:/calendario?ano=" + salvo.getData().getYear() + "&mes=" + salvo.getData().getMonthValue();
        } catch (ClienteObrigatorioException e) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("lembretesTexto", lembretesTexto);
            model.addAttribute("clientes", clienteUseCase.listar(new FiltroCliente(null, null, null, true, null, null)));
            return "calendario/form";
        } catch (EventoNaoEncontradoException | AcessoNegadoException e) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("erro", "Nao foi possivel salvar: " + e.getMessage());
            model.addAttribute("lembretesTexto", lembretesTexto);
            model.addAttribute("clientes", clienteUseCase.listar(new FiltroCliente(null, null, null, true, null, null)));
            return "calendario/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id,
                           @RequestParam int ano, @RequestParam int mes,
                           @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
                           RedirectAttributes redirectAttributes) {
        try {
            eventoUseCase.excluir(id, usuarioAutenticado.getUsuarioResumo(), administrador(usuarioAutenticado));
        } catch (EventoNaoEncontradoException | AcessoNegadoException e) {
            redirectAttributes.addFlashAttribute("erroCalendario", "Nao foi possivel excluir: " + e.getMessage());
        }
        return "redirect:/calendario?ano=" + ano + "&mes=" + mes;
    }

    @PostMapping("/serie/{serieId}/pausar")
    public String pausarSerie(@PathVariable Long serieId,
                               @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
                               RedirectAttributes redirectAttributes) {
        try {
            eventoUseCase.pausarSerie(serieId, usuarioAutenticado.getUsuarioResumo(), administrador(usuarioAutenticado));
        } catch (EventoNaoEncontradoException | AcessoNegadoException e) {
            redirectAttributes.addFlashAttribute("erroCalendario", "Nao foi possivel pausar a recorrencia: " + e.getMessage());
            return "redirect:/calendario";
        }
        return "redirect:/calendario/" + serieId + "/editar";
    }

    private boolean administrador(UsuarioAutenticado usuarioAutenticado) {
        return usuarioAutenticado.getTipoAcesso() == TipoAcesso.ADMINISTRADOR;
    }

    /** Converte "3, 4" em [3, 4] -- ignora entradas invalidas/em branco. */
    private List<Integer> parseLembretes(String texto) {
        List<Integer> lembretes = new ArrayList<>();
        if (texto == null || texto.isBlank()) return lembretes;

        for (String parte : texto.split(",")) {
            String valor = parte.trim();
            if (valor.isEmpty()) continue;
            try {
                int dias = Integer.parseInt(valor);
                if (dias > 0) lembretes.add(dias);
            } catch (NumberFormatException ignorado) {
                // Entrada invalida no campo -- ignora silenciosamente em vez de travar o formulario.
            }
        }
        return lembretes;
    }

    private String formatarLembretes(List<Integer> lembretes) {
        if (lembretes == null || lembretes.isEmpty()) return "";
        return lembretes.stream().map(String::valueOf).collect(Collectors.joining(", "));
    }

    /**
     * Monta a grade mensal como lista de semanas (cada semana com 7 dias,
     * de domingo a sabado), incluindo os dias do mes anterior/seguinte que
     * completam a primeira e a ultima semana -- exatamente como uma grade
     * de calendario tipo Google Calendar.
     */
    private List<List<LocalDate>> montarSemanas(YearMonth mesAtual) {
        LocalDate primeiroDia = mesAtual.atDay(1);
        LocalDate ultimoDia = mesAtual.atEndOfMonth();

        // DayOfWeek.SUNDAY = 7 no padrao ISO -- convertemos para 0 (domingo) a 6 (sabado).
        int deslocamentoInicio = primeiroDia.getDayOfWeek() == DayOfWeek.SUNDAY ? 0 : primeiroDia.getDayOfWeek().getValue();
        LocalDate inicioGrade = primeiroDia.minusDays(deslocamentoInicio);

        int deslocamentoFim = ultimoDia.getDayOfWeek() == DayOfWeek.SUNDAY ? 6 : 6 - ultimoDia.getDayOfWeek().getValue();
        LocalDate fimGrade = ultimoDia.plusDays(deslocamentoFim);

        List<List<LocalDate>> semanas = new ArrayList<>();
        List<LocalDate> semanaAtual = new ArrayList<>();
        LocalDate dia = inicioGrade;
        while (!dia.isAfter(fimGrade)) {
            semanaAtual.add(dia);
            if (semanaAtual.size() == 7) {
                semanas.add(semanaAtual);
                semanaAtual = new ArrayList<>();
            }
            dia = dia.plusDays(1);
        }
        return semanas;
    }
}
