package com.empresa.sistema.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Usado para a SENHA DE LOGIN dos funcionarios (nao confundir com a
        // criptografia AES das senhas de portais, que fica em CriptografiaConverter).
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/login").permitAll()

                // Gerenciar usuarios e permissoes: so administrador
                .requestMatchers("/usuarios/**").hasRole("ADMINISTRADOR")
                // Log de auditoria: so administrador consulta
                .requestMatchers("/auditoria/**").hasRole("ADMINISTRADOR")

                // Ver a senha de uma credencial: liberado para qualquer funcionario
                // autenticado -- e uso interno da empresa, todo mundo pode consultar.
                // Precisa vir ANTES da regra mais ampla de credenciais logo abaixo,
                // senao essa regra mais restrita pegaria primeiro.
                .requestMatchers("/credenciais/*/revelar").authenticated()

                // Gerenciar credenciais de portais (adicionar, remover): so
                // administrador ou editor_senhas -- ver a senha e liberado (regra
                // acima), mas cadastrar/excluir credencial continua restrito.
                .requestMatchers("/clientes/*/credenciais/**", "/credenciais/**")
                    .hasAnyRole("ADMINISTRADOR", "EDITOR_SENHAS")

                // Baixar um anexo de cliente: liberado para qualquer funcionario
                // autenticado -- mesma logica das senhas de portais, uso interno.
                // Precisa vir ANTES da regra de gerenciar anexos logo abaixo.
                .requestMatchers("/anexos/*/baixar").authenticated()

                // Gerenciar anexos de cliente (adicionar, remover): mesma regra de
                // quem pode editar o cadastro do cliente.
                .requestMatchers("/clientes/*/anexos/**", "/anexos/**")
                    .hasAnyRole("ADMINISTRADOR", "EDITOR", "EDITOR_SENHAS")

                // Gerenciar fornecedores (adicionar, remover, reconsultar situacao):
                // mesma regra de quem pode editar o cadastro do cliente. Ver a
                // lista continua liberada pra todo mundo (fica na tela de detalhe).
                .requestMatchers("/clientes/*/fornecedores/**", "/fornecedores/**")
                    .hasAnyRole("ADMINISTRADOR", "EDITOR", "EDITOR_SENHAS")

                // Importar/remover NF-e (Fase 3 da reforma tributaria): mesma regra de
                // quem pode editar o cadastro do cliente. Ver a lista e o resumo de
                // credito continua liberado pra todo mundo (fica na tela de detalhe).
                .requestMatchers("/clientes/*/notas-fiscais/**", "/notas-fiscais/**")
                    .hasAnyRole("ADMINISTRADOR", "EDITOR", "EDITOR_SENHAS")

                // NF-e de venda e faturamento manual (Fase 4 da reforma tributaria):
                // mesma regra das NF-e de compra acima.
                .requestMatchers("/clientes/*/notas-fiscais-saida/**", "/notas-fiscais-saida/**", "/clientes/*/faturamento")
                    .hasAnyRole("ADMINISTRADOR", "EDITOR", "EDITOR_SENHAS")

                // Cadastrar/editar clientes, CNAEs e codigos de servico: qualquer editor
                .requestMatchers("/clientes/novo", "/clientes/*/editar", "/clientes/*/excluir",
                        "/clientes/*/cnaes/**", "/clientes/*/codigos/**")
                    .hasAnyRole("ADMINISTRADOR", "EDITOR", "EDITOR_SENHAS")

                // Importar/exportar Excel: qualquer editor
                .requestMatchers("/clientes/importar").hasAnyRole("ADMINISTRADOR", "EDITOR", "EDITOR_SENHAS")

                // Consulta e exportacao: todo mundo autenticado, inclusive visualizador
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?saiu")
            );
            // CSRF continua ATIVO de propósito, mesmo sendo intranet -- o Thymeleaf
            // ja inclui o token automaticamente em formularios com th:action.

        return http.build();
    }
}
