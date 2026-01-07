ALTER TABLE clientes
ADD COLUMN usuario_id BINARY(16) NULL;

CREATE INDEX idx_clientes_usuario_id ON clientes(usuario_id);

ALTER TABLE clientes
ADD CONSTRAINT fk_clientes_usuario
FOREIGN KEY (usuario_id) REFERENCES usuarios(id);