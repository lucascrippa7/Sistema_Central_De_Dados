package com.empresa.sistema.infrastructure.web.controller;

import com.empresa.sistema.application.port.in.ConsultarCnpjUseCase;
import com.empresa.sistema.domain.model.DadosCnpj;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint JSON usado pelo formulario de cliente pra puxar dados de um
 * CNPJ (razao social, nome fantasia, cidade, UF) via BrasilAPI, evitando
 * digitar tudo na mao. So consulta -- nao salva nada aqui.
 */
@RestController
@RequestMapping("/api/cnpj")
public class CnpjController {

    private final ConsultarCnpjUseCase consultarCnpjUseCase;

    public CnpjController(ConsultarCnpjUseCase consultarCnpjUseCase) {
        this.consultarCnpjUseCase = consultarCnpjUseCase;
    }

    @GetMapping("/{cnpj}")
    public ResponseEntity<DadosCnpj> consultar(@PathVariable String cnpj) {
        return consultarCnpjUseCase.consultar(cnpj)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
