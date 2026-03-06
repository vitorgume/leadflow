CREATE TABLE setores (
    id_setor BINARY(16) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(255),
    data_criacao DATETIME NOT NULL,
    usuario_id BINARY(16) NOT NULL,
    PRIMARY KEY (id_setor),
    CONSTRAINT fk_setores_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);