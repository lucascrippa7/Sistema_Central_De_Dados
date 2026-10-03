package com.empresa.sistema.infrastructure.security;

import com.empresa.sistema.application.port.in.AutenticacaoUseCase;
import com.empresa.sistema.domain.exception.UsuarioNaoEncontradoException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Este e o UNICO ponto do sistema onde o Spring Security "encosta" na
 * aplicacao. Ele traduz o contrato do Spring (UserDetailsService) para
 * o caso de uso de dominio (AutenticacaoUseCase).
 */
@Service
public class SpringSecurityUserDetailsService implements UserDetailsService {

    private final AutenticacaoUseCase autenticacaoUseCase;

    public SpringSecurityUserDetailsService(AutenticacaoUseCase autenticacaoUseCase) {
        this.autenticacaoUseCase = autenticacaoUseCase;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        try {
            return new UsuarioAutenticado(autenticacaoUseCase.carregarPorLogin(login));
        } catch (UsuarioNaoEncontradoException e) {
            throw new UsernameNotFoundException("Usuario ou senha invalidos", e);
        }
    }
}
