-- Eventos de calendario: entregas de imposto, reunioes e eventos gerais.
--
-- usuario_id e o "dono" do evento (quem criou) -- e o que permite cada
-- funcionario ver so os proprios eventos, com o administrador vendo todos.
--
-- cliente_id e opcional no banco porque so e obrigatorio para o tipo
-- ENTREGA_IMPOSTO -- essa regra e validada na aplicacao (EventoApplicationService),
-- nao aqui, ja que depende do valor de outra coluna (tipo).
CREATE TABLE eventos (
    id                  BIGSERIAL PRIMARY KEY,
    titulo              VARCHAR(200) NOT NULL,
    descricao           TEXT,
    tipo                VARCHAR(30)  NOT NULL,   -- ENTREGA_IMPOSTO, REUNIAO, EVENTO
    data                DATE         NOT NULL,
    horario             TIME,
    cliente_id          BIGINT REFERENCES clientes(id) ON DELETE CASCADE,
    usuario_id          BIGINT       NOT NULL REFERENCES usuarios(id),
    criado_em           TIMESTAMP    NOT NULL DEFAULT now(),
    criado_por_id       BIGINT REFERENCES usuarios(id),
    atualizado_em       TIMESTAMP,
    atualizado_por_id   BIGINT REFERENCES usuarios(id)
);

CREATE INDEX idx_eventos_data ON eventos(data);
CREATE INDEX idx_eventos_usuario ON eventos(usuario_id);
CREATE INDEX idx_eventos_cliente ON eventos(cliente_id);
