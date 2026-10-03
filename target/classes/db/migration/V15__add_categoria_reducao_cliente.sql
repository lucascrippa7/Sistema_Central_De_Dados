-- Fase 4 do modulo de reforma tributaria: liga o cliente a uma categoria
-- de reducao de aliquota da LC 214/2025 (ver V14) -- usado no comparativo
-- de regimes tributarios. Nulo = alíquota cheia (mesmo efeito de SEM_REDUCAO).
ALTER TABLE clientes ADD COLUMN categoria_reducao_lc214 VARCHAR(30) REFERENCES reducao_aliquota_lc214(codigo);
