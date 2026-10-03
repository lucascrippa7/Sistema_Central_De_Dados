-- ==========================================================
-- Usuario administrador inicial, so para o primeiro acesso.
--
-- Login: admin
-- Senha temporaria: TrocarSenha123!
--
-- IMPORTANTE: troque essa senha assim que entrar no sistema pela
-- primeira vez (a troca de senha pelo proprio usuario e uma tela
-- a ser construida no modulo de Usuarios, no proximo passo).
-- ==========================================================
INSERT INTO usuarios (nome, login, senha_hash, cargo, tipo_acesso, ativo)
VALUES (
    'Administrador',
    'admin',
    '$2b$12$pfuL6ODdzFHxvhxg0QZWvOejhLh7b8DD5xKdPcOaOuasLz028bxEO',
    'Administrador do sistema',
    'ADMINISTRADOR',
    TRUE
);
