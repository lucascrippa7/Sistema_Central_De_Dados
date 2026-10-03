-- Corrige o tipo da coluna "estado" em clientes: CHAR(2) e VARCHAR(2)
-- sao tratados como tipos diferentes pelo Hibernate, mesmo guardando
-- o mesmo dado (a sigla da UF). O mapeamento das entidades Java espera
-- VARCHAR, entao ajustamos a coluna para bater com o codigo.
ALTER TABLE clientes ALTER COLUMN estado TYPE VARCHAR(2);
