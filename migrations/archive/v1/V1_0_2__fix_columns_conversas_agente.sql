ALTER TABLE conversas_agente DROP COLUMN status;

ALTER TABLE conversas_agente ADD COLUMN status tinyint DEFAULT NULL;