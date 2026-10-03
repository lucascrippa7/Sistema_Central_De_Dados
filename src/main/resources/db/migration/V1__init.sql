-- ==========================================================
-- Schema inicial: usuarios, clientes, credenciais e auditoria
-- ==========================================================

CREATE TABLE usuarios (
    id              BIGSERIAL PRIMARY KEY,
    nome            VARCHAR(150) NOT NULL,
    login           VARCHAR(60)  NOT NULL UNIQUE,
    senha_hash      VARCHAR(100) NOT NULL,
    cargo           VARCHAR(100),
    tipo_acesso     VARCHAR(20)  NOT NULL,   -- VISUALIZADOR, EDITOR, ADMINISTRADOR
    ativo           BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em       TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE clientes (
    id                  BIGSERIAL PRIMARY KEY,
    razao_social        VARCHAR(200) NOT NULL,
    nome_fantasia       VARCHAR(200),
    regime_tributario   VARCHAR(60),
    cidade              VARCHAR(100),
    estado              CHAR(2),
    tipo_servico        VARCHAR(150),
    descricao_servico   TEXT,
    ativo               BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em           TIMESTAMP NOT NULL DEFAULT now(),
    criado_por_id       BIGINT REFERENCES usuarios(id),
    atualizado_em       TIMESTAMP,
    atualizado_por_id   BIGINT REFERENCES usuarios(id)
);

CREATE TABLE cliente_cnae (
    id          BIGSERIAL PRIMARY KEY,
    cliente_id  BIGINT NOT NULL REFERENCES clientes(id) ON DELETE CASCADE,
    codigo      VARCHAR(15) NOT NULL,
    descricao   VARCHAR(255),
    principal   BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE cliente_codigo_servico (
    id          BIGSERIAL PRIMARY KEY,
    cliente_id  BIGINT NOT NULL REFERENCES clientes(id) ON DELETE CASCADE,
    codigo      VARCHAR(30) NOT NULL,
    descricao   VARCHAR(255)
);

-- Credenciais de portais externos (Giss Online, Ginfes, Nota do Milhao, etc.)
-- O campo "senha" e armazenado CRIPTOGRAFADO pela aplicacao (AES), nunca em texto puro.
CREATE TABLE credenciais (
    id                  BIGSERIAL PRIMARY KEY,
    cliente_id          BIGINT NOT NULL REFERENCES clientes(id) ON DELETE CASCADE,
    sistema             VARCHAR(100) NOT NULL,   -- ex: 'Giss Online', 'Ginfes', 'Nota do Milhao'
    url_portal          VARCHAR(300),
    usuario_portal      VARCHAR(150),
    senha_criptografada VARCHAR(500),
    observacoes         TEXT,
    criado_em           TIMESTAMP NOT NULL DEFAULT now(),
    criado_por_id       BIGINT REFERENCES usuarios(id),
    atualizado_em       TIMESTAMP,
    atualizado_por_id   BIGINT REFERENCES usuarios(id)
);

-- Log de auditoria: quem fez o que, quando, em qual registro
CREATE TABLE log_auditoria (
    id              BIGSERIAL PRIMARY KEY,
    usuario_id      BIGINT REFERENCES usuarios(id),
    entidade        VARCHAR(60)  NOT NULL,   -- ex: 'CLIENTE', 'CREDENCIAL', 'USUARIO'
    entidade_id     BIGINT,
    acao            VARCHAR(30)  NOT NULL,   -- CRIACAO, EDICAO, EXCLUSAO, VISUALIZACAO
    detalhes        TEXT,
    data_hora       TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_clientes_estado ON clientes(estado);
CREATE INDEX idx_credenciais_cliente ON credenciais(cliente_id);
CREATE INDEX idx_log_entidade ON log_auditoria(entidade, entidade_id);
CREATE INDEX idx_log_usuario ON log_auditoria(usuario_id);
