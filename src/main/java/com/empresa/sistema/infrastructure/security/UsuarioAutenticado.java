package com.empresa.sistema.infrastructure.security;

import com.empresa.sistema.domain.model.TipoAcesso;
import com.empresa.sistema.domain.model.Usuario;
import com.empresa.sistema.domain.model.UsuarioResumo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Faz a ponte entre o Usuario de dominio e o UserDetails que o Spring
 * Security exige. Esta classe SO existe na infraestrutura -- e por isso
 * que o Usuario de dominio nao precisa (e nao deve) implementar
 * UserDetails diretamente.
 */
public class UsuarioAutenticado implements UserDetails {

    private final Usuario usuario;

    public UsuarioAutenticado(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public UsuarioResumo getUsuarioResumo() {
        return usuario.paraResumo();
    }

    public TipoAcesso getTipoAcesso() {
        return usuario.getTipoAcesso();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getTipoAcesso().name()));
    }

    @Override
    public String getPassword() {
        return usuario.getSenhaHash();
    }

    @Override
    public String getUsername() {
        return usuario.getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return usuario.isAtivo();
    }
}
