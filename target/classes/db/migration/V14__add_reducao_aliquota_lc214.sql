-- Fase 4 do modulo de reforma tributaria: tabela de referencia das faixas
-- de reducao de aliquota de IBS/CBS previstas na LC 214/2025.
--
-- IMPORTANTE (mesmo espirito da tabela de CNAE, V9): isso NAO e a lista
-- exaustiva da lei (que tem varios anexos com centenas de NCM/codigos de
-- servico especificos) -- e um recorte com as categorias mais comuns pra
-- um escritorio de contabilidade, com a base legal de cada uma, pra dar
-- um ponto de partida no comparativo de regime. Sempre que o enquadramento
-- de um cliente especifico for duvidoso, precisa checar o anexo da lei.
--
-- Regra importante do calculo (Art. 7-A da LC 227/2026): as reducoes NAO
-- se acumulam -- se mais de uma faixa poderia se aplicar, vale so a maior.
--
-- Aliquota cheia de referencia (padrao nacional estimado, ainda em
-- transicao ate 2033): CBS 8,80% + IBS 17,70% = 26,50%.

CREATE TABLE reducao_aliquota_lc214 (
    codigo               VARCHAR(30) PRIMARY KEY,
    categoria            VARCHAR(150) NOT NULL,
    percentual_reducao   NUMERIC(5,2) NOT NULL,
    base_legal           VARCHAR(100) NOT NULL,
    descricao            VARCHAR(500) NOT NULL
);

INSERT INTO reducao_aliquota_lc214 (codigo, categoria, percentual_reducao, base_legal, descricao) VALUES
    ('CESTA_BASICA', 'Cesta básica nacional de alimentos', 100.00, 'Art. 125, Anexo I',
        'Itens da Cesta Básica Nacional de Alimentos listados no Anexo I -- alíquota zero de IBS e CBS, com manutenção dos créditos das etapas anteriores (Art. 52).'),
    ('HORTIFRUTI', 'Produtos hortícolas, frutas e ovos', 100.00, 'Art. 148, Anexo XV',
        'Produtos hortícolas, frutas e ovos listados no Anexo XV -- alíquota zero de IBS e CBS.'),
    ('ALIMENTOS_CONSUMO_HUMANO', 'Demais alimentos destinados ao consumo humano', 60.00, 'Art. 135, 137, Anexo VII',
        'Alimentos de consumo humano que não entraram na cesta básica (Anexo I), listados no Anexo VII -- inclui itens da agricultura familiar.'),
    ('SAUDE', 'Serviços de saúde', 60.00, 'Art. 128, Anexo III',
        'Serviços de saúde listados no Anexo III (consultas, exames, planos de saúde, etc.).'),
    ('EDUCACAO', 'Serviços de educação', 60.00, 'Art. 128, Anexo II',
        'Serviços de educação listados no Anexo II (ensino regular, cursos, etc.).'),
    ('DISPOSITIVOS_MEDICOS', 'Dispositivos médicos', 60.00, 'Art. 128, Anexo IV',
        'Dispositivos médicos listados no Anexo IV.'),
    ('ACESSIBILIDADE', 'Dispositivos de acessibilidade para pessoas com deficiência', 60.00, 'Art. 128, Anexo V',
        'Dispositivos de acessibilidade listados no Anexo V.'),
    ('NUTRICAO_ESPECIAL', 'Nutrição enteral/parenteral e fórmulas para erros inatos do metabolismo', 60.00, 'Art. 128, Anexo VI',
        'Composições nutricionais especiais listadas no Anexo VI.'),
    ('PROFISSAO_REGULAMENTADA', 'Serviços de profissão intelectual regulamentada', 30.00, 'Art. 127',
        'Serviços prestados por pessoa física habilitada por conselho profissional (advocacia, engenharia, contabilidade, medicina, etc., quando prestados como pessoa física ou sociedade uniprofissional nos termos do Art. 127, §1º).'),
    ('SEGURANCA_NACIONAL', 'Segurança nacional, da informação e cibernética', 60.00, 'Art. 128',
        'Bens e serviços relacionados à soberania e segurança nacional, segurança da informação e segurança cibernética.'),
    ('SEM_REDUCAO', 'Nenhuma redução -- alíquota cheia', 0.00, '-',
        'Uso padrão quando a atividade do cliente não se enquadra em nenhuma das reduções acima -- alíquota cheia de IBS/CBS (~26,50% na fase final de transição).');
