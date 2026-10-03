-- Fase 2 do modulo de reforma tributaria: fornecedores de um cliente, com
-- alerta basico de regularidade (situacao cadastral na Receita). Ainda nao
-- calcula credito real -- isso e Fase 3 (leitura de XML de NF-e).
CREATE TABLE fornecedores (
    id                  BIGSERIAL PRIMARY KEY,
    cliente_id          BIGINT NOT NULL REFERENCES clientes(id) ON DELETE CASCADE,
    cnpj                VARCHAR(18) NOT NULL,
    razao_social        VARCHAR(200),
    regime_tributario   VARCHAR(60),
    situacao_cadastral  VARCHAR(30),
    ultima_consulta_em  TIMESTAMP,
    criado_em           TIMESTAMP NOT NULL DEFAULT now(),
    criado_por_id       BIGINT REFERENCES usuarios(id)
);

CREATE INDEX idx_fornecedores_cliente_id ON fornecedores(cliente_id);
