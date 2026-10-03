package com.empresa.sistema.infrastructure.web.controller;

import com.empresa.sistema.domain.exception.AcessoNegadoException;
import com.empresa.sistema.domain.exception.AnexoNaoEncontradoException;
import com.empresa.sistema.domain.exception.ClienteNaoEncontradoException;
import com.empresa.sistema.domain.exception.CredencialNaoEncontradaException;
import com.empresa.sistema.domain.exception.EventoNaoEncontradoException;
import com.empresa.sistema.domain.exception.FornecedorNaoEncontradoException;
import com.empresa.sistema.domain.exception.UsuarioNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Rede de seguranca contra a "Whitelabel Error Page" do Spring: qualquer
 * excecao que escape de um controller (porque o desenvolvedor esqueceu de
 * tratar aquele caso especifico) cai aqui em vez de derrubar a tela.
 *
 * Isso NAO substitui o tratamento local que ja existe em alguns controllers
 * (ex: EventoController capturando ClienteObrigatorioException pra mostrar
 * erro dentro do proprio formulario) -- aquele tratamento continua rodando
 * primeiro e e melhor, porque sabe exatamente o que fazer. Este handler
 * global e so o "e se ninguem pensou nesse caso ainda" -- garante que o
 * pior que pode acontecer e uma mensagem de erro amigavel, nunca uma tela
 * tecnica em branco.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** "Isso que voce tentou abrir nao existe (mais)." -- manda de volta pra uma lista com aviso. */
    @ExceptionHandler({ClienteNaoEncontradoException.class, UsuarioNaoEncontradoException.class,
            EventoNaoEncontradoException.class, CredencialNaoEncontradaException.class,
            AnexoNaoEncontradoException.class, FornecedorNaoEncontradoException.class})
    public String naoEncontrado(RuntimeException e, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        log.warn("Recurso nao encontrado ao acessar {}: {}", request.getRequestURI(), e.getMessage());
        redirectAttributes.addFlashAttribute("erroGeral", e.getMessage());
        return "redirect:" + paginaDeRetorno(request);
    }

    /** "Isso e de outra pessoa, voce nao pode ver/mexer." */
    @ExceptionHandler(AcessoNegadoException.class)
    public String acessoNegado(AcessoNegadoException e, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        log.warn("Acesso negado ao acessar {}: {}", request.getRequestURI(), e.getMessage());
        redirectAttributes.addFlashAttribute("erroGeral", e.getMessage());
        return "redirect:" + paginaDeRetorno(request);
    }

    /**
     * Qualquer outra coisa que a gente nao previu (bug de verdade, erro de
     * banco, etc). Aqui a gente nao sabe pra onde mandar o usuario de volta
     * com seguranca, entao mostra uma pagina de erro amigavel (com a navbar,
     * pra nao deixar a pessoa "presa" sem menu) em vez de redirecionar.
     */
    @ExceptionHandler(Exception.class)
    public String erroInesperado(Exception e, HttpServletRequest request, Model model) {
        log.error("Erro nao tratado ao processar {}", request.getRequestURI(), e);
        model.addAttribute("mensagemErro", "Ocorreu um erro inesperado ao processar essa acao. "
                + "Tente novamente -- se continuar acontecendo, avise o administrador do sistema.");
        return "erro-generico";
    }

    /** Pra onde mandar o usuario de volta, baseado em que area do sistema ele estava. */
    private String paginaDeRetorno(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.startsWith("/calendario")) return "/calendario";
        if (uri.startsWith("/clientes")) return "/clientes";
        if (uri.startsWith("/usuarios")) return "/usuarios";
        if (uri.startsWith("/auditoria")) return "/auditoria";
        return "/dashboard";
    }
}
