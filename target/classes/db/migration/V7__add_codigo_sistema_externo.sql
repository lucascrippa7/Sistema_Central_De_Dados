-- Codigo desse cliente em outro sistema usado pela empresa (ex: Dominio).
-- Texto livre, so pra facilitar busca cruzada -- nao tem formato fixo
-- porque cada sistema externo numera do seu jeito.
ALTER TABLE clientes ADD COLUMN codigo_sistema_externo VARCHAR(50);

CREATE INDEX idx_clientes_codigo_sistema_externo ON clientes(codigo_sistema_externo);
