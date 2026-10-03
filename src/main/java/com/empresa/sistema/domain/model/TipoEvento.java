package com.empresa.sistema.domain.model;

/**
 * Tipos de evento do calendario.
 *
 * ENTREGA_IMPOSTO - entrega/obrigacao fiscal, sempre vinculada a um cliente.
 * REUNIAO         - reuniao interna ou com cliente (vinculo a cliente e opcional).
 * EVENTO          - evento generico (vinculo a cliente e opcional).
 */
public enum TipoEvento {
    ENTREGA_IMPOSTO,
    REUNIAO,
    EVENTO
}
