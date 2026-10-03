package com.empresa.sistema.infrastructure.persistence.mapper;

import com.empresa.sistema.domain.model.Usuario;
import com.empresa.sistema.domain.model.UsuarioResumo;
import com.empresa.sistema.infrastructure.persistence.entity.UsuarioJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toDomain(UsuarioJpaEntity entity) {
        if (entity == null) return null;
        Usuario usuario = new Usuario();
        usuario.setId(entity.getId());
        usuario.setNome(entity.getNome());
        usuario.setLogin(entity.getLogin());
        usuario.setEmail(entity.getEmail());
        usuario.setSenhaHash(entity.getSenhaHash());
        usuario.setCargo(entity.getCargo());
        usuario.setTipoAcesso(entity.getTipoAcesso());
        usuario.setAtivo(entity.isAtivo());
        usuario.setCriadoEm(entity.getCriadoEm());
        return usuario;
    }

    public UsuarioResumo toResumo(UsuarioJpaEntity entity) {
        if (entity == null) return null;
        return new UsuarioResumo(entity.getId(), entity.getNome(), entity.getLogin());
    }

    /**
     * Copia os campos do Usuario de dominio para dentro da entidade JPA.
     * Note que "senha" (texto puro) nunca e tocada aqui -- so senhaHash,
     * que ja deve ter sido calculado pela aplicacao antes de chegar aqui.
     */
    public void aplicarNaEntidade(Usuario usuario, UsuarioJpaEntity entity) {
        entity.setNome(usuario.getNome());
        entity.setLogin(usuario.getLogin());
        entity.setEmail(usuario.getEmail());
        entity.setSenhaHash(usuario.getSenhaHash());
        entity.setCargo(usuario.getCargo());
        entity.setTipoAcesso(usuario.getTipoAcesso());
        entity.setAtivo(usuario.isAtivo());
        entity.setCriadoEm(usuario.getCriadoEm());
    }

    /**
     * Cria uma "referencia" de entidade so com o ID preenchido -- usada
     * para gravar uma associacao (ex: cliente.criadoPor) sem precisar
     * carregar o UsuarioJpaEntity inteiro do banco.
     */
    public UsuarioJpaEntity referenciaPorId(Long id) {
        if (id == null) return null;
        UsuarioJpaEntity ref = new UsuarioJpaEntity();
        ref.setId(id);
        return ref;
    }
}
