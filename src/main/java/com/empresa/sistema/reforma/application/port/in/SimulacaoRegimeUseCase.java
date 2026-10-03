package com.empresa.sistema.reforma.application.port.in;

import com.empresa.sistema.reforma.domain.model.ResultadoSimulacaoRegime;
import com.empresa.sistema.reforma.domain.model.SimulacaoRegimeInput;

/**
 * Caso de uso da simulacao de regime tributario (reforma CBS/IBS).
 * Porta de entrada -- o controller so conhece esta interface, igual
 * o padrao ja usado em ClienteUseCase/UsuarioUseCase no resto do sistema.
 */
public interface SimulacaoRegimeUseCase {

    ResultadoSimulacaoRegime simular(SimulacaoRegimeInput input);
}
