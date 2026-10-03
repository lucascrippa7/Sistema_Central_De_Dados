package com.empresa.sistema.application.port.in;

import com.empresa.sistema.domain.model.DadosCnpj;

import java.util.Optional;

public interface ConsultarCnpjUseCase {
    /** Aceita CNPJ formatado ou nao -- limpa e valida antes de consultar. Vazio se invalido ou nao encontrado. */
    Optional<DadosCnpj> consultar(String cnpj);
}
