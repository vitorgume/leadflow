-- =========================================================================
-- MIGRATION: Alterar PK de outros_contatos de BIGINT (Auto Increment) para BINARY(16) (UUID)
-- =========================================================================

-- Passo 1: Remover o comportamento de AUTO_INCREMENT da coluna atual para podermos manipulá-la
ALTER TABLE outros_contatos MODIFY COLUMN id_outro_contato BIGINT NOT NULL;

-- Passo 2: Remover a restrição de Chave Primária atual
ALTER TABLE outros_contatos DROP PRIMARY KEY;

-- Passo 3: Criar uma nova coluna temporária para armazenar os UUIDs
ALTER TABLE outros_contatos ADD COLUMN novo_id BINARY(16);

-- Passo 4: Gerar UUIDs automaticamente para todos os registros que já existem na tabela
UPDATE outros_contatos SET novo_id = UUID_TO_BIN(UUID());

-- Passo 5: Garantir que a nova coluna não aceite valores nulos
ALTER TABLE outros_contatos MODIFY COLUMN novo_id BINARY(16) NOT NULL;

-- Passo 6: Apagar a coluna BIGINT antiga
ALTER TABLE outros_contatos DROP COLUMN id_outro_contato;

-- Passo 7: Renomear a nova coluna com os UUIDs para assumir o nome oficial
ALTER TABLE outros_contatos RENAME COLUMN novo_id TO id_outro_contato;

-- Passo 8: Coroar a nova coluna BINARY(16) como a Chave Primária oficial da tabela
ALTER TABLE outros_contatos ADD PRIMARY KEY (id_outro_contato);