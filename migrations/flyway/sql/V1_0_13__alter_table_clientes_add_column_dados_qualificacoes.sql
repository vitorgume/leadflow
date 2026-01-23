-- 1. Adiciona a nova coluna de atributos dinâmicos (JSON)
ALTER TABLE clientes
ADD COLUMN atributos_qualificacao JSON DEFAULT NULL;

-- 2. TRANSFORMAÇÃO DA PRIMARY KEY (De Long para UUID)
-- ATENÇÃO: Se tiver dados, faça backup antes!

-- Remove a auto-incremento (se existir) e a PK antiga
ALTER TABLE clientes MODIFY id BIGINT; 
ALTER TABLE clientes DROP PRIMARY KEY;

-- Opcional: Se quiser remover a coluna antiga 'id' para substituir pela nova
ALTER TABLE clientes DROP COLUMN id;

-- Cria a nova coluna de ID como UUID (VARCHAR 36)
ALTER TABLE clientes ADD COLUMN id_cliente VARCHAR(36) NOT NULL;

-- Define ela como a nova Primary Key
ALTER TABLE clientes ADD PRIMARY KEY (id_cliente);

-- 3. Ajuste na FK de Usuário (Garantindo que bata com o UUID do Usuário)
-- Se a coluna usuario_id antiga era BIGINT, precisamos mudar para VARCHAR(36) também
-- Primeiro removemos a FK antiga (descubra o nome com 'SHOW CREATE TABLE clientes')
-- ALTER TABLE clientes DROP FOREIGN KEY fk_clientes_usuarios; 

-- Ajustamos o tipo
ALTER TABLE clientes MODIFY COLUMN usuario_id VARCHAR(36);

-- Recriamos a FK
ALTER TABLE clientes 
ADD CONSTRAINT fk_clientes_usuarios 
FOREIGN KEY (usuario_id) REFERENCES usuarios(id);