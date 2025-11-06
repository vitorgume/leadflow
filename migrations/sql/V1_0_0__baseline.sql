DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `inativo` bit(1) NOT NULL,
  `regiao` tinyint DEFAULT NULL,
  `segmento` tinyint DEFAULT NULL,
  `id_cliente` binary(16) NOT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `telefone` varchar(255) DEFAULT NULL,
  `descricao_material` varchar(255) DEFAULT NULL,
  `canal` tinyint DEFAULT NULL,
  PRIMARY KEY (`id_cliente`),
  CONSTRAINT `clientes_chk_1` CHECK ((`regiao` between 0 and 3)),
  CONSTRAINT `clientes_chk_2` CHECK ((`segmento` between 0 and 6)),
  CONSTRAINT `clientes_chk_3` CHECK ((`canal` between 0 and 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `administradores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `administradores` (
  `id` binary(16) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `senha` varchar(255) DEFAULT NULL,
  `telefone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `conversas_agente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `conversas_agente` (
  `id_conversa` binary(16) NOT NULL,
  `data_criacao` datetime(6) DEFAULT NULL,
  `finalizada` bit(1) DEFAULT NULL,
  `inativa` bit(1) DEFAULT NULL,
  `cliente_id_cliente` binary(16) DEFAULT NULL,
  `vendedor_id_vendedor` bigint DEFAULT NULL,
  `data_ultima_mensagem` datetime(6) DEFAULT NULL,
  `recontato` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id_conversa`),
  UNIQUE KEY `UKkoswwkurhtundj6gs1rtq8t95` (`cliente_id_cliente`),
  KEY `FKgaynr6vui45qff47snif7mxb8` (`vendedor_id_vendedor`),
  CONSTRAINT `FK1a0gtx2idaegeyiirc2xf7dh8` FOREIGN KEY (`cliente_id_cliente`) REFERENCES `clientes` (`id_cliente`),
  CONSTRAINT `FKgaynr6vui45qff47snif7mxb8` FOREIGN KEY (`vendedor_id_vendedor`) REFERENCES `vendedores` (`id_vendedor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `mensagens_conversa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mensagens_conversa` (
  `id_mensagem_conversa` binary(16) NOT NULL,
  `responsavel` varchar(50) NOT NULL,
  `conteudo` varchar(2000) NOT NULL,
  `id_conversa` binary(16) NOT NULL,
  `data` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id_mensagem_conversa`),
  KEY `id_conversa` (`id_conversa`),
  CONSTRAINT `mensagens_conversa_ibfk_1` FOREIGN KEY (`id_conversa`) REFERENCES `conversas_agente` (`id_conversa`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `outros_contatos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `outros_contatos` (
  `setor` tinyint DEFAULT NULL,
  `id_outro_contato` bigint NOT NULL AUTO_INCREMENT,
  `descricao` varchar(255) DEFAULT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `telefone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_outro_contato`),
  CONSTRAINT `outros_contatos_chk_1` CHECK ((`setor` between 0 and 3))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `vendedores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendedores` (
  `inativo` bit(1) DEFAULT NULL,
  `id_vendedor` bigint NOT NULL AUTO_INCREMENT,
  `nome` varchar(255) DEFAULT NULL,
  `telefone` varchar(255) DEFAULT NULL,
  `regioes` varbinary(255) DEFAULT NULL,
  `segmentos` varbinary(255) DEFAULT NULL,
  `prioritario` bit(1) DEFAULT NULL,
  `valor` int DEFAULT NULL,
  PRIMARY KEY (`id_vendedor`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;