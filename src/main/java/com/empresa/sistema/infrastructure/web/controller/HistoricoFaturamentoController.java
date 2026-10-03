package com.empresa.sistema.infrastructure.web.controller;

import com.empresa.sistema.application.port.in.HistoricoFaturamentoUseCase;
import com.empresa.sistema.infrastructure.security.UsuarioAutenticado;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/** Informar/corrigir faturamento manual por ano -- mesma restricao de edicao de cliente. */
@Controller
public class HistoricoFaturamentoController {

    private final HistoricoFaturamentoUseCase historicoFaturamentoUseCase;

    public HistoricoFaturamentoController(HistoricoFaturamentoUseCase historicoFaturamentoUseCase) {
        this.historicoFaturamentoUseCase = historicoFaturamentoUseCase;
    }

    @PostMapping("/clientes/{clienteId}/faturamento")
    public String definirManual(@PathVariable Long clienteId,
                                 @RequestParam int ano,
                                 @RequestParam(required = false) BigDecimal valor,
                                 @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        historicoFaturamentoUseCase.definirManual(clienteId, ano, valor, usuarioAutenticado.getUsuarioResumo());
        return "redirect:/clientes/" + clienteId;
    }
}
