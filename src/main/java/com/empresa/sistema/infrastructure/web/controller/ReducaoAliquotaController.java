package com.empresa.sistema.infrastructure.web.controller;

import com.empresa.sistema.application.port.in.ReducaoAliquotaUseCase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Tela de consulta da tabela de reducao de aliquota da LC 214/2025 -- Fase 4
 * do modulo de reforma tributaria. So leitura por enquanto: ainda nao esta
 * ligada ao cadastro do cliente nem ao comparativo de regimes (proximo passo).
 */
@Controller
@RequestMapping("/reforma-tributaria/reducoes-lc214")
public class ReducaoAliquotaController {

    private final ReducaoAliquotaUseCase reducaoAliquotaUseCase;

    public ReducaoAliquotaController(ReducaoAliquotaUseCase reducaoAliquotaUseCase) {
        this.reducaoAliquotaUseCase = reducaoAliquotaUseCase;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("reducoes", reducaoAliquotaUseCase.listar());
        return "reforma-tributaria/reducoes-lc214";
    }
}
