package com.empresa.sistema.application.port.in;

import com.empresa.sistema.domain.model.ColunaUsuarioPlanilha;

import java.util.Set;

public interface UsuarioExcelUseCase {
    /** "colunas" e o que a pessoa escolheu ver alem do Nome; nulo/vazio = todas as colunas. */
    byte[] exportar(Set<ColunaUsuarioPlanilha> colunas);
}
