CREATE TABLE usuarios (
    id CHAR(36) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    telefone VARCHAR(20),
    senha VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_usuarios_email (email)
);
