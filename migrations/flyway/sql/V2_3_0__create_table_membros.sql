CREATE TABLE membros (
    id BINARY(16) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    usuario_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_membros_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);