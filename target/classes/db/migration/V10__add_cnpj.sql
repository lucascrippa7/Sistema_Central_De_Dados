-- CNPJ do cliente -- opcional (clientes ja cadastrados ficam sem, podem
-- preencher depois), mas unico quando preenchido (nao deixa cadastrar o
-- mesmo CNPJ duas vezes). Guardamos como veio (formatado ou so numeros),
-- sem mascara fixa no banco.
ALTER TABLE clientes ADD COLUMN cnpj VARCHAR(18);
CREATE UNIQUE INDEX idx_clientes_cnpj ON clientes(cnpj) WHERE cnpj IS NOT NULL;
