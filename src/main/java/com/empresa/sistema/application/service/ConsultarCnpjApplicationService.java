package com.empresa.sistema.application.service;

import com.empresa.sistema.application.port.in.ConsultarCnpjUseCase;
import com.empresa.sistema.application.port.out.ConsultaCnpjPort;
import com.empresa.sistema.domain.model.DadosCnpj;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConsultarCnpjApplicationService implements ConsultarCnpjUseCase {

    private final ConsultaCnpjPort consultaCnpjPort;

    public ConsultarCnpjApplicationService(ConsultaCnpjPort consultaCnpjPort) {
        this.consultaCnpjPort = consultaCnpjPort;
    }

    @Override
    public Optional<DadosCnpj> consultar(String cnpj) {
        String cnpjLimpo = cnpj == null ? "" : cnpj.replaceAll("[^0-9]", "");
        if (cnpjLimpo.length() != 14) {
            return Optional.empty();
        }
        return consultaCnpjPort.consultar(cnpjLimpo);
    }
}
