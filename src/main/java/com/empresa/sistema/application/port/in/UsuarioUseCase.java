package com.empresa.sistema.application.port.in;

import com.empresa.sistema.domain.model.Usuario;
import com.empresa.sistema.domain.model.UsuarioResumo;

import java.util.List;

public interface UsuarioUseCase {

    List<UsuarioResumo> listarAtivos();

    List<Usuario> listarTodos();

    Usuario buscarPorId(Long id);

    Usuario criar(Usuario usuario, UsuarioResumo usuarioLogado);

    Usuario atualizar(Long id, Usuario dadosAtualizados, UsuarioResumo usuarioLogado);

    void redefinirSenha(Long id, String novaSenha, UsuarioResumo usuarioLogado);

    void trocarPropriaSenha(Long usuarioId, String senhaAtual, String novaSenha);

    /**
     * So o e-mail -- usado na tela "Minha conta", disponivel pra qualquer
     * funcionario autenticado. Nao reaproveita o atualizar(...) de cima de
     * proposito: aquele mexe em login/cargo/tipoAcesso tambem, e nao
     * queremos abrir brecha pra alguem mudar o proprio tipo de acesso.
     */
    void atualizarProprioEmail(Long usuarioId, String novoEmail);
}
