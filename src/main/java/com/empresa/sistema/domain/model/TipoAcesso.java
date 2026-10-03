package com.empresa.sistema.domain.model;

/**
 * Perfis de permissao do sistema.
 *
 * Ver a senha de uma credencial de portal e liberado para qualquer
 * funcionario autenticado, independente do tipo de acesso -- e uso interno
 * da empresa. O que os tipos abaixo controlam e o resto: editar cadastro
 * de cliente e gerenciar (adicionar/remover) as credenciais.
 *
 * VISUALIZADOR   - so consulta e exporta dados; nao cadastra/edita nada.
 * EDITOR         - cadastra/edita clientes, mas nao gerencia credenciais de portais.
 * EDITOR_SENHAS  - como EDITOR, e tambem pode adicionar/remover credenciais de portais.
 * ADMINISTRADOR  - acesso total, incluindo gerenciar usuarios e permissoes.
 */
public enum TipoAcesso {
    VISUALIZADOR,
    EDITOR,
    EDITOR_SENHAS,
    ADMINISTRADOR
}
