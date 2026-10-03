-- Adiciona inscricao municipal, inscricao estadual, e o responsavel pela
-- empresa (um usuario do sistema, nao um campo de texto livre).
ALTER TABLE clientes ADD COLUMN inscricao_municipal VARCHAR(30);
ALTER TABLE clientes ADD COLUMN inscricao_estadual VARCHAR(30);
ALTER TABLE clientes ADD COLUMN responsavel_id BIGINT REFERENCES usuarios(id);
