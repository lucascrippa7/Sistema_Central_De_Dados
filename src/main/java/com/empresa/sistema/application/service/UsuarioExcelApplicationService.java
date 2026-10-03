package com.empresa.sistema.application.service;

import com.empresa.sistema.application.port.in.UsuarioExcelUseCase;
import com.empresa.sistema.application.port.in.UsuarioUseCase;
import com.empresa.sistema.application.port.out.GeradorPlanilhaUsuariosPort;
import com.empresa.sistema.domain.model.ColunaUsuarioPlanilha;
import com.empresa.sistema.domain.model.Usuario;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class UsuarioExcelApplicationService implements UsuarioExcelUseCase {

    private final UsuarioUseCase usuarioUseCase;
    private final GeradorPlanilhaUsuariosPort geradorPlanilhaUsuariosPort;

    public UsuarioExcelApplicationService(UsuarioUseCase usuarioUseCase, GeradorPlanilhaUsuariosPort geradorPlanilhaUsuariosPort) {
        this.usuarioUseCase = usuarioUseCase;
        this.geradorPlanilhaUsuariosPort = geradorPlanilhaUsuariosPort;
    }

    @Override
    public byte[] exportar(Set<ColunaUsuarioPlanilha> colunas) {
        List<Usuario> usuarios = usuarioUseCase.listarTodos();
        Set<ColunaUsuarioPlanilha> colunasEfetivas = (colunas == null || colunas.isEmpty())
                ? EnumSet.allOf(ColunaUsuarioPlanilha.class)
                : colunas;
        return geradorPlanilhaUsuariosPort.gerar(usuarios, colunasEfetivas);
    }
}
