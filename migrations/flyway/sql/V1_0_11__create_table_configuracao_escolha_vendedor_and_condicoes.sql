-- 1. Cria a tabela principal de configuração
CREATE TABLE configuracoes_escolha_vendedor (
    id_configuracao_escolha_vendedor VARCHAR(36) NOT NULL,
    usuario_id VARCHAR(36), -- Assumindo que seu Usuario usa UUID/VARCHAR
    prioridade INT,
    PRIMARY KEY (id_configuracao_escolha_vendedor),
    CONSTRAINT fk_config_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 2. Cria a tabela de condições (Filhos)
CREATE TABLE condicoes (
    id_condicao VARCHAR(36) NOT NULL,
    campo VARCHAR(255),
    operador_logico VARCHAR(50),
    valor VARCHAR(255),
    conector_logico VARCHAR(50),
    -- Coluna de FK para ligar com a configuração sem precisar de tabela extra
    id_configuracao_escolha_vendedor VARCHAR(36), 
    PRIMARY KEY (id_condicao),
    CONSTRAINT fk_condicao_config FOREIGN KEY (id_configuracao_escolha_vendedor) REFERENCES configuracoes_escolha_vendedor(id_configuracao_escolha_vendedor) ON DELETE CASCADE
);

-- 3. Cria a tabela associativa (ManyToMany) para os Vendedores
CREATE TABLE configuracao_vendedores (
    id_configuracao_escolha_vendedor VARCHAR(36) NOT NULL,
    id_vendedor BIGINT NOT NULL, -- O ID do vendedor é Long (BIGINT)
    PRIMARY KEY (id_configuracao_escolha_vendedor, id_vendedor),
    CONSTRAINT fk_cv_config FOREIGN KEY (id_configuracao_escolha_vendedor) REFERENCES configuracoes_escolha_vendedor(id_configuracao_escolha_vendedor),
    CONSTRAINT fk_cv_vendedor FOREIGN KEY (id_vendedor) REFERENCES vendedores(id_vendedor)
);