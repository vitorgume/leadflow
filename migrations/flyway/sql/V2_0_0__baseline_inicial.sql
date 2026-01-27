-- Baseline V2.0.0
-- Este script contém a estrutura completa e atualizada do banco de dados.

-- Tabela de usuários, contendo informações de login e configuração.
CREATE TABLE usuarios (
    id BINARY(16) NOT NULL PRIMARY KEY,
    nome VARCHAR(255),
    telefone VARCHAR(255),
    senha VARCHAR(255),
    email VARCHAR(255),
    telefone_conectado VARCHAR(255),
    atributos_qualificacao JSON,
    mensagem_direcionamento_vendedor VARCHAR(255),
    mensagem_recontato_g1 VARCHAR(255),
    whatsapp_token VARCHAR(255),
    whatsapp_id_instance VARCHAR(255),
    agente_api_key VARCHAR(255),
    -- Campos da entidade embutida ConfiguracaoCrmEntity
    crm_type VARCHAR(255),
    mapeamento_campos JSON,
    id_tag_inativo VARCHAR(255),
    id_tag_ativo VARCHAR(255),
    id_etapa_inativos VARCHAR(255),
    id_etapa_ativos VARCHAR(255),
    acess_token TEXT
);

-- Tabela de vendedores que serão associados aos clientes.
CREATE TABLE vendedores (
    id_vendedor BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255),
    telefone VARCHAR(255),
    inativo BOOLEAN,
    id_vendedor_crm INT,
    padrao BOOLEAN
);

-- Tabela para outros contatos diversos.
CREATE TABLE outros_contatos (
    id_outro_contato BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255),
    telefone VARCHAR(255),
    descricao VARCHAR(255)
);

-- Tabela de clientes, que são os leads principais.
CREATE TABLE clientes (
    id_cliente BINARY(16) NOT NULL PRIMARY KEY,
    nome VARCHAR(255),
    telefone VARCHAR(255),
    atributos_qualificacao JSON,
    inativo BOOLEAN NOT NULL,
    usuario_id BINARY(16),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabela de configurações para a lógica de escolha de vendedor.
CREATE TABLE configuracoes_escolha_vendedor (
    id_configuracao_escolha_vendedor BINARY(16) NOT NULL PRIMARY KEY,
    usuario_id BINARY(16),
    prioridade INT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabela de junção para a relação N-N entre configurações e vendedores.
CREATE TABLE configuracao_vendedores (
    id_configuracao_escolha_vendedor BINARY(16) NOT NULL,
    id_vendedor BIGINT NOT NULL,
    PRIMARY KEY (id_configuracao_escolha_vendedor, id_vendedor),
    FOREIGN KEY (id_configuracao_escolha_vendedor) REFERENCES configuracoes_escolha_vendedor(id_configuracao_escolha_vendedor),
    FOREIGN KEY (id_vendedor) REFERENCES vendedores(id_vendedor)
);

-- Tabela de condições lógicas para a escolha de vendedor.
CREATE TABLE condicoes (
    id_condicao BINARY(16) NOT NULL PRIMARY KEY,
    campo VARCHAR(255),
    operador_logico VARCHAR(255),
    valor VARCHAR(255),
    conector_logico VARCHAR(255),
    id_configuracao_escolha_vendedor BINARY(16),
    FOREIGN KEY (id_configuracao_escolha_vendedor) REFERENCES configuracoes_escolha_vendedor(id_configuracao_escolha_vendedor)
);

-- Tabela para gerenciar as conversas com o agente.
CREATE TABLE conversas_agente (
    id_conversa BINARY(16) NOT NULL PRIMARY KEY,
    data_criacao DATETIME,
    data_ultima_mensagem DATETIME,
    finalizada BOOLEAN,
    recontato BOOLEAN,
    status INT,
    cliente_id_cliente BINARY(16) UNIQUE,
    vendedor_id_vendedor BIGINT,
    FOREIGN KEY (cliente_id_cliente) REFERENCES clientes(id_cliente),
    FOREIGN KEY (vendedor_id_vendedor) REFERENCES vendedores(id_vendedor)
);

-- Tabela para armazenar as mensagens de cada conversa.
CREATE TABLE mensagens_conversa (
    id_mensagem_conversa BINARY(16) NOT NULL PRIMARY KEY,
    responsavel VARCHAR(255),
    conteudo VARCHAR(255),
    data DATETIME,
    id_conversa BINARY(16) NOT NULL,
    FOREIGN KEY (id_conversa) REFERENCES conversas_agente(id_conversa)
);

CREATE TABLE prompts_usuarios (
    id BINARY(16) NOT NULL,
    id_usuario BINARY(16) NOT NULL,
    titulo VARCHAR(255),
    prompt TEXT NOT NULL,
    
    PRIMARY KEY (id),
    
    -- Constraint para garantir que o usuário existe
    CONSTRAINT fk_prompts_usuarios 
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

CREATE TABLE base_conhecimento_usuarios (
    id BINARY(16) NOT NULL,
    id_usuario BINARY(16) NOT NULL,
    titulo VARCHAR(255),
    conteudo TEXT NOT NULL,
    
    PRIMARY KEY (id),
    
    -- Constraint para garantir que o usuário existe
    CONSTRAINT fk_base_conhecimento_usuarios 
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);
