-- 1. DESLIGAR SEGURANÇA PARA LIMPEZA
SET FOREIGN_KEY_CHECKS = 0;

-- 2. LIMPAR TABELAS RELACIONADAS
-- (Necessário para evitar erros de integridade ao mexer nos IDs)
TRUNCATE TABLE mensagens_conversa;
TRUNCATE TABLE conversas_agente;

-- 3. RECRIAÇÃO TOTAL DA TABELA CLIENTES
-- Em vez de tentar consertar a tabela velha, jogamos fora e fazemos uma nova.
DROP TABLE IF EXISTS clientes;

CREATE TABLE clientes (
    id_cliente BINARY(16) NOT NULL,
    nome VARCHAR(255),
    telefone VARCHAR(255),
    inativo BIT(1),
    atributos_qualificacao JSON DEFAULT NULL,
    usuario_id BINARY(16),
    
    PRIMARY KEY (id_cliente),
    
    -- Recriando a FK para usuários
    CONSTRAINT fk_clientes_usuarios_v7 
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

-- 4. AJUSTAR A TABELA FILHA (CONVERSAS_AGENTE)
-- Ajustamos a coluna para bater com o novo CHAR(36) da tabela clientes
ALTER TABLE conversas_agente MODIFY COLUMN cliente_id_cliente BINARY(16);

-- Recriamos a ligação (FK) entre Conversas e Clientes
-- Nota: O DROP FOREIGN KEY não é necessário pois o TRUNCATE e a recriação do pai invalidam o cache
ALTER TABLE conversas_agente 
ADD CONSTRAINT FK_conversas_agente_cliente_v7
FOREIGN KEY (cliente_id_cliente) REFERENCES clientes (id_cliente);

-- 5. RELIGAR SEGURANÇA
SET FOREIGN_KEY_CHECKS = 1;