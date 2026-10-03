package com.empresa.sistema.reforma.infrastructure.web.controller;

import com.empresa.sistema.application.port.in.ClienteUseCase;
import com.empresa.sistema.reforma.application.port.in.ComparativoRegimeUseCase;
import com.empresa.sistema.reforma.domain.model.ComparativoRegimeInput;
import com.empresa.sistema.reforma.domain.model.SetorEconomico;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Year;

/** Comparativo de regimes tributarios de um cliente -- Fase 4 do modulo de reforma tributaria. */
@Controller
@RequestMapping("/clientes/{clienteId}/comparativo-regime")
public class ComparativoRegimeController {

    private final ComparativoRegimeUseCase comparativoRegimeUseCase;
    private final ClienteUseCase clienteUseCase;

    public ComparativoRegimeController(ComparativoRegimeUseCase comparativoRegimeUseCase, ClienteUseCase clienteUseCase) {
        this.comparativoRegimeUseCase = comparativoRegimeUseCase;
        this.clienteUseCase = clienteUseCase;
    }

    @GetMapping
    public String formulario(@PathVariable Long clienteId,
                              @RequestParam(required = false) Integer ano,
                              @RequestParam(required = false) SetorEconomico setor,
                              @RequestParam(required = false) java.math.BigDecimal aliquotaIss,
                              @RequestParam(required = false) java.math.BigDecimal margemLucroEstimada,
                              Model model) {
        int anoUsado = ano != null ? ano : Year.now().getValue();

        ComparativoRegimeInput input = new ComparativoRegimeInput();
        if (setor != null) input.setSetor(setor);
        if (aliquotaIss != null) input.setAliquotaIss(aliquotaIss);
        if (margemLucroEstimada != null) input.setMargemLucroEstimada(margemLucroEstimada);

        model.addAttribute("cliente", clienteUseCase.buscarPorId(clienteId));
        model.addAttribute("ano", anoUsado);
        model.addAttribute("input", input);
        model.addAttribute("resultado", comparativoRegimeUseCase.calcular(clienteId, anoUsado, input));
        return "reforma-tributaria/comparativo-cliente";
    }
}
