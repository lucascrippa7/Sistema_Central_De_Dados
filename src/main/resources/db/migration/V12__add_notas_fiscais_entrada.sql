-- Fase 3 do modulo de reforma tributaria: NF-e de entrada importadas via
-- XML, com credito de ICMS/PIS/COFINS calculado por item (ver
-- RegrasCreditoTributario -- regra generica, ponto de partida ajustavel).

CREATE TABLE notas_fiscais_entrada (
    id                       BIGSERIAL PRIMARY KEY,
    cliente_id               BIGINT NOT NULL REFERENCES clientes(id) ON DELETE CASCADE,
    chave_acesso             VARCHAR(44) NOT NULL,
    numero                   VARCHAR(20),
    serie                    VARCHAR(10),
    data_emissao             DATE,
    fornecedor_cnpj          VARCHAR(18),
    fornecedor_razao_social  VARCHAR(200),
    valor_total_nota         NUMERIC(15,2) NOT NULL DEFAULT 0,
    nome_arquivo_original    VARCHAR(255),
    importado_em             TIMESTAMP NOT NULL DEFAULT now(),
    importado_por_id         BIGINT REFERENCES usuarios(id)
);

-- Uma mesma nota nao pode ser importada duas vezes para o mesmo cliente.
CREATE UNIQUE INDEX idx_notas_fiscais_entrada_cliente_chave ON notas_fiscais_entrada(cliente_id, chave_acesso);
CREATE INDEX idx_notas_fiscais_entrada_data_emissao ON notas_fiscais_entrada(cliente_id, data_emissao);

CREATE TABLE itens_nota_fiscal_entrada (
    id                BIGSERIAL PRIMARY KEY,
    nota_fiscal_id    BIGINT NOT NULL REFERENCES notas_fiscais_entrada(id) ON DELETE CASCADE,
    numero_item       INTEGER,
    descricao_produto VARCHAR(500),
    ncm               VARCHAR(10),
    cfop              VARCHAR(10),
    cst_icms          VARCHAR(5),
    cst_pis           VARCHAR(5),
    cst_cofins        VARCHAR(5),
    valor_produto     NUMERIC(15,2) NOT NULL DEFAULT 0,
    valor_icms        NUMERIC(15,2) NOT NULL DEFAULT 0,
    valor_pis         NUMERIC(15,2) NOT NULL DEFAULT 0,
    valor_cofins      NUMERIC(15,2) NOT NULL DEFAULT 0,
    credito_icms      NUMERIC(15,2) NOT NULL DEFAULT 0,
    credito_pis       NUMERIC(15,2) NOT NULL DEFAULT 0,
    credito_cofins    NUMERIC(15,2) NOT NULL DEFAULT 0
);

CREATE INDEX idx_itens_nota_fiscal_entrada_nota_id ON itens_nota_fiscal_entrada(nota_fiscal_id);
