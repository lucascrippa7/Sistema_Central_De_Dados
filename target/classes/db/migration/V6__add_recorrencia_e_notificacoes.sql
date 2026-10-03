-- E-mail do usuario, usado so para receber notificacoes de eventos (opcional).
ALTER TABLE usuarios ADD COLUMN email VARCHAR(150);

-- Recorrencia mensal de eventos (ex: entrega de imposto todo dia X de cada
-- mes). O evento "mestre" da serie e aquele cujo id == serie_id -- so ele
-- carrega a configuracao valida da serie (serie_ativa, dia_mes). As demais
-- ocorrencias geradas soh apontam pra serie (serie_id) e guardam a qual
-- ano/mes elas correspondem (serie_ano_referencia/serie_mes_referencia),
-- independente da data ter sido editada depois pelo usuario.
ALTER TABLE eventos ADD COLUMN recorrente BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE eventos ADD COLUMN serie_id BIGINT REFERENCES eventos(id) ON DELETE CASCADE;
ALTER TABLE eventos ADD COLUMN serie_ativa BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE eventos ADD COLUMN dia_mes INTEGER;
ALTER TABLE eventos ADD COLUMN serie_ano_referencia INTEGER;
ALTER TABLE eventos ADD COLUMN serie_mes_referencia INTEGER;

-- Controle de envio de e-mail: evita notificar o mesmo evento duas vezes no mesmo dia.
ALTER TABLE eventos ADD COLUMN notificacao_email_enviada_em DATE;

-- Dias de antecedencia extras para notificar, alem do proprio dia do evento (ex: 3, 4).
CREATE TABLE evento_lembretes (
    evento_id   BIGINT  NOT NULL REFERENCES eventos(id) ON DELETE CASCADE,
    dias_antes  INTEGER NOT NULL
);

CREATE INDEX idx_eventos_serie ON eventos(serie_id);
CREATE INDEX idx_evento_lembretes_evento ON evento_lembretes(evento_id);
