-- Anexos de cliente (contrato social, procuracao, etc), guardados direto no
-- banco (bytea) -- decisao consciente pra simplificar backup (tudo junto).
CREATE TABLE anexos_cliente (
    id              BIGSERIAL PRIMARY KEY,
    cliente_id      BIGINT NOT NULL REFERENCES clientes(id) ON DELETE CASCADE,
    nome_arquivo    VARCHAR(255) NOT NULL,
    tipo_conteudo   VARCHAR(100),
    tamanho_bytes   BIGINT NOT NULL,
    conteudo        BYTEA NOT NULL,
    criado_em       TIMESTAMP NOT NULL DEFAULT now(),
    criado_por_id   BIGINT REFERENCES usuarios(id)
);

CREATE INDEX idx_anexos_cliente_cliente_id ON anexos_cliente(cliente_id);
