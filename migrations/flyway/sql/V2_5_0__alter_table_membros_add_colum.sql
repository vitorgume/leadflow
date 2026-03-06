ALTER TABLE membros ADD COLUMN setor_id BINARY(16);
ALTER TABLE membros ADD CONSTRAINT fk_membros_setor FOREIGN KEY (setor_id) REFERENCES setores (id_setor);