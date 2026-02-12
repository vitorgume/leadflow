#############################
# Variáveis – Monorepo (STG/PR/PROD)
#############################

# Região AWS (ex.: us-east-1)
variable "region" {
  type = string
}

# Prefixo para nomear recursos (ex.: "ura-chatbot-ia-stg" ou "ura-chatbot-ia-pr-123")
variable "name_prefix" {
  type = string
}

# Tags padrão para FinOps/Governança
variable "tags" {
  type    = map(string)
  default = {}
}

#############################
# SQS (FIFO)
#############################
# Nome da fila FIFO
variable "sqs_name" {
  type    = string
  default = "entradas.fifo"
}

#############################
# DynamoDB
#############################
# Tabela 1: contexto_entity (com GSI TelefoneStatusIndex)
variable "dynamo_contexto_table" {
  type    = string
  default = "contexto_entity_leadflow_teste"
}

# Tabela 2: outro_contato_entity (com GSI TelefoneStatusIndex)
variable "dynamo_outro_contato_table" {
  type    = string
  default = "outro_contato_entity_leadflow_teste"
}

#############################
# RDS MySQL (Staging simples)
#############################
# Nome do database (schema)
variable "rds_db_name" {
  type    = string
  default = "app_stg"
}

# Usuário do MySQL
variable "rds_username" {
  type    = string
  default = "appuser"
}

# Classe da instância (custo baixo p/ stg)
variable "rds_instance_class" {
  type    = string
  default = "db.t4g.micro"
}

# Armazenamento alocado (GB)
variable "rds_allocated_storage" {
  type    = number
  default = 20
}

# Tornar público (true facilita stg; em prod prefira false + bastion/VPN)
variable "rds_publicly_accessible" {
  type    = bool
  default = true
}

# Lista de CIDRs com acesso ao MySQL (troque "0.0.0.0/0" pelo seu IP no stg)
variable "mysql_allowed_cidrs" {
  type    = list(string)
  default = ["0.0.0.0/0"]
}

#############################
# Imagens (ECR) – 3 serviços App Runner
#############################
# Conta e repositório ECR
variable "account_id" {
  type = string
}

variable "ecr_repository" {
  type = string
  # ex.: "ura-chatbot-ia"
}

# Tags das imagens (definidas pelo CI conforme serviço/branch/PR)
variable "image_tag_api_intermediaria" {
  type = string
  # ex.: "stg-inter" ou "pr-123-<sha>-inter"
}

variable "image_tag_api_agente" {
  type = string
  # ex.: "stg-agent" ou "pr-123-<sha>-agent"
}

variable "image_tag_api_principal" {
  type = string
  # ex.: "stg-principal" ou "pr-123-<sha>-principal"
}

#############################
# Segredos / Chaves (Secrets Manager)
#############################
# API do Agente (FastAPI)
variable "APP_SECURITY_ENCRYPTION_KEY" {
  description = "Chave de criptografia para a API Agente"
  type        = string
  sensitive   = true
}

#############################
# API Principal (Java/Spring)
#############################
# URL do CRM (não sensível por padrão)

# Chaves da API Principal (sensíveis)
variable "URA_CHATBOT_IA_SECRET_KEY" {
  description = "Secret key para autenticação JWT"
  type = string
  sensitive = true
  default = ""
}

variable "API_PRINCIPAL_API_KEY" {
  type      = string
  sensitive = true
  default   = ""
}

variable "API_PRINCIPAL_SECRET_KEY" {
  type      = string
  sensitive = true
  default   = ""
}

# Credenciais/segredos de WhatsApp (sensíveis)
variable "WHASTAPP_CLIENT_TOKEN" {
  type      = string
  sensitive = true
  default   = ""
}

variable "URA_CHATBOT_IA_API_KEY" {
  type      = string
  sensitive = true
  default   = ""
}

#############################
# Portas expostas pelos serviços
#############################
variable "api_intermediaria_port" {
  type    = number
  default = 8081
}

variable "api_agente_port" {
  type    = number
  default = 8000
}

variable "api_principal_port" {
  type    = number
  default = 8080
}

variable "create_services" {
  description = "Se true, cria os serviços do App Runner. Se false, apenas infra (RDS, VPC connector, SG, secrets, SQS, Dynamo, IAM, etc.)"
  type        = bool
  default     = true
}



