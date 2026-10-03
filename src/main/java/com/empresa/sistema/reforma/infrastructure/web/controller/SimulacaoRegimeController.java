package com.empresa.sistema.reforma.infrastructure.web.controller;

import com.empresa.sistema.reforma.application.port.in.SimulacaoRegimeUseCase;
import com.empresa.sistema.reforma.domain.model.SimulacaoRegimeInput;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller do simulador de regime tributario (reforma CBS/IBS).
 * So conhece a porta de entrada SimulacaoRegimeUseCase -- igual o
 * padrao do ClienteController, nao conhece a implementacao do calculo.
 */
@Controller
@RequestMapping("/reforma-tributaria")
public class SimulacaoRegimeController {

    private final SimulacaoRegimeUseCase simulacaoRegimeUseCase;

    public SimulacaoRegimeController(SimulacaoRegimeUseCase simulacaoRegimeUseCase) {
        this.simulacaoRegimeUseCase = simulacaoRegimeUseCase;
    }

    @GetMapping
    public String formulario(Model model) {
        model.addAttribute("simulacaoRegimeInput", new SimulacaoRegimeInput());
        return "reforma-tributaria/simulador";
    }

    @PostMapping("/simular")
    public String simular(@Valid @ModelAttribute("simulacaoRegimeInput") SimulacaoRegimeInput input,
                           BindingResult resultado,
                           Model model) {
        if (resultado.hasErrors()) {
            return "reforma-tributaria/simulador";
        }
        model.addAttribute("resultado", simulacaoRegimeUseCase.simular(input));
        return "reforma-tributaria/simulador";
    }
}
