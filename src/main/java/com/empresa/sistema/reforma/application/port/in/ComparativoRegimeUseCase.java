package com.empresa.sistema.reforma.application.port.in;

import com.empresa.sistema.reforma.domain.model.ComparativoRegimeInput;
import com.empresa.sistema.reforma.domain.model.ResultadoComparativoRegime;

public interface ComparativoRegimeUseCase {
    ResultadoComparativoRegime calcular(Long clienteId, int ano, ComparativoRegimeInput input);
}
