package com.empresa.sistema.application.port.out;

import com.empresa.sistema.domain.model.ColunaUsuarioPlanilha;
import com.empresa.sistema.domain.model.Usuario;

import java.util.List;
import java.util.Set;

public interface GeradorPlanilhaUsuariosPort {
    byte[] gerar(List<Usuario> usuarios, Set<ColunaUsuarioPlanilha> colunas);
}
