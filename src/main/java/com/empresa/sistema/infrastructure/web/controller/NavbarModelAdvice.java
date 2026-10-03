package com.empresa.sistema.infrastructure.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Injeta a URI da requisicao atual no modelo de TODA tela renderizada pelo
 * sistema, com o nome "uriAtual".
 *
 * Motivo de existir: a navbar (fragments/navbar.html) precisa saber em que
 * secao o usuario esta para destacar o item de menu certo. O jeito antigo
 * de fazer isso em Thymeleaf era usar "${#request.requestURI}" direto no
 * template, mas essa versao do Thymeleaf desativou esse objeto utilitario
 * por padrao (por seguranca) -- ver o erro "'request'...no longer
 * available by default for template expressions". Este ControllerAdvice e
 * a alternativa recomendada: buscar o dado no lado do controller/Spring
 * (aqui) e so consumir o valor pronto no template.
 */
@ControllerAdvice
public class NavbarModelAdvice {

    @ModelAttribute("uriAtual")
    public String uriAtual(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
