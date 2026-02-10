-- 1. LIMPEZA DE SEGURANÇA
-- Necessário pois vamos adicionar uma coluna NOT NULL (id_usuario).
-- Se a tabela tiver dados antigos sem usuário, a migration falharia.
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE outros_contatos;
SET FOREIGN_KEY_CHECKS = 1;

-- 2. ADICIONAR AS COLUNAS
ALTER TABLE outros_contatos
ADD COLUMN tipo_contato VARCHAR(50), -- Para o Enum
ADD COLUMN id_usuario BINARY(16) NOT NULL; -- Obrigatório (optional = false)

-- 3. CRIAR O RELACIONAMENTO (FOREIGN KEY)
ALTER TABLE outros_contatos
ADD CONSTRAINT fk_outro_contato_usuario
FOREIGN KEY (id_usuario) REFERENCES usuarios(id);