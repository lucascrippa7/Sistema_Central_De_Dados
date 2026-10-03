-- Fase 4 do modulo de reforma tributaria: faturamento historico por cliente,
-- pra comparar regimes tributarios (Simples/Presumido/Real/Hibrido) com base
-- na LC 214/2025. NF-e de saida (venda) alimenta o calculo automatico;
-- historico_faturamento guarda a correcao/complemento manual por ano.

CREATE TABLE notas_fiscais_saida (
    id                        BIGSERIAL PRIMARY KEY,
    cliente_id                BIGINT NOT NULL REFERENCES clientes(id) ON DELETE CASCADE,
    chave_acesso              VARCHAR(44) NOT NULL,
    numero                    VARCHAR(20),
    serie                     VARCHAR(10),
    data_emissao              DATE NOT NULL,
    destinatario_cnpj         VARCHAR(18),
    destinatario_razao_social VARCHAR(200),
    valor_total_nota          NUMERIC(15,2) NOT NULL DEFAULT 0,
    nome_arquivo_original     VARCHAR(255),
    importado_em              TIMESTAMP NOT NULL DEFAULT now(),
    importado_por_id          BIGINT REFERENCES usuarios(id)
);

CREATE UNIQUE INDEX idx_notas_fiscais_saida_cliente_chave ON notas_fiscais_saida(cliente_id, chave_acesso);
CREATE INDEX idx_notas_fiscais_saida_data_emissao ON notas_fiscais_saida(cliente_id, data_emissao);

CREATE TABLE historico_faturamento (
    id                  BIGSERIAL PRIMARY KEY,
    cliente_id          BIGINT NOT NULL REFERENCES clientes(id) ON DELETE CASCADE,
    ano                 INTEGER NOT NULL,
    valor_faturamento   NUMERIC(15,2) NOT NULL DEFAULT 0,
    atualizado_em       TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_por_id   BIGINT REFERENCES usuarios(id),
    UNIQUE (cliente_id, ano)
);
