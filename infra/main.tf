locals {
  labels         = merge(var.tags, { app = var.name_prefix })
  db_host        = aws_db_instance.mysql.address
  # JDBC para as APIs Java (Spring)
  jdbc_url       = "jdbc:mysql://${local.db_host}:3306/${var.rds_db_name}?useSSL=false&allowPublicKeyRetrieval=true"
  # SQLAlchemy para a API Python (FastAPI)
  sqlalchemy_url = "mysql+pymysql://${var.rds_username}:${random_password.rds_appuser.result}@${local.db_host}:3306/${var.rds_db_name}"
}

# =========================
# SQS FIFO (com timeouts)
# =========================
resource "aws_sqs_queue" "fifo" {
  name                        = var.sqs_name
  fifo_queue                  = true
  content_based_deduplication = true

  visibility_timeout_seconds = 180
  delay_seconds              = 30
  receive_wait_time_seconds  = 15

  tags = local.labels
}

# =========================
# DynamoDB (com GSI TelefoneStatusIndex)
# =========================
resource "aws_dynamodb_table" "contexto" {
  name         = var.dynamo_contexto_table
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "id"

  attribute { 
    name = "id"       
    type = "S" 
  }

  attribute { 
    name = "telefone" 
    type = "S" 
  }

  attribute { 
    name = "status"   
    type = "S" 
  }

  global_secondary_index {
    name            = "TelefoneStatusIndex"
    hash_key        = "telefone"
    range_key       = "status"
    projection_type = "ALL"
  }

  tags = local.labels
}

resource "aws_dynamodb_table" "outro_contato" {
  name         = var.dynamo_outro_contato_table
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "id"

  attribute { 
    name = "id"       
    type = "S" 
  }

  attribute { 
    name = "telefone" 
    type = "S" 
  }
  
  attribute { 
    name = "status"   
    type = "S" 
  }

  global_secondary_index {
    name            = "TelefoneStatusIndex"
    hash_key        = "telefone"
    range_key       = "status"
    projection_type = "ALL"
  }

  tags = local.labels
}

# =========================
# VPC / Subnets (default)
# =========================
data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

# Lê cada subnet para obter availability_zone_id
data "aws_subnet" "default_by_id" {
  for_each = toset(data.aws_subnets.default.ids)
  id       = each.value
}

# ========= Filtros para VPC Connector (App Runner) =========
variable "apprunner_az_blocklist" {
  type        = list(string)
  description = "AZ IDs bloqueadas para App Runner VPC Connector"
  default     = ["use1-az3"] # a do erro atual
}

variable "apprunner_subnet_id_blocklist" {
  type        = list(string)
  description = "Subnet IDs a bloquear no VPC Connector"
  default     = []  # ex.: ["subnet-0123abcd", "subnet-0456efgh"]
}

locals {
  # Subnets válidas: exclui AZs bloqueadas e subnets bloqueadas
  apprunner_subnet_ids = [
    for s in data.aws_subnet.default_by_id :
    s.id
    if !contains(var.apprunner_az_blocklist, s.availability_zone_id)
    && !contains(var.apprunner_subnet_id_blocklist, s.id)
  ]

  # (opcional) AZs distintas após o filtro, útil p/ sanity-check
  apprunner_subnet_az_ids = distinct([
    for s in data.aws_subnet.default_by_id :
    s.availability_zone_id
    if !contains(var.apprunner_az_blocklist, s.availability_zone_id)
    && !contains(var.apprunner_subnet_id_blocklist, s.id)
  ])
}

# =========================
# RDS MySQL (staging)
# =========================
resource "aws_db_subnet_group" "this" {
  name       = "${var.name_prefix}-mysql-subnets"
  subnet_ids = data.aws_subnets.default.ids
  tags       = local.labels
}

resource "aws_security_group" "mysql" {
  name        = "${var.name_prefix}-mysql-sg"
  description = "Allow MySQL inbound"
  vpc_id      = data.aws_vpc.default.id

  dynamic "ingress" {
    for_each = var.mysql_allowed_cidrs
    content {
      description = "MySQL from allowed CIDR"
      from_port   = 3306
      to_port     = 3306
      protocol    = "tcp"
      cidr_blocks = [ingress.value]
    }
  }

  egress {
    description = "All egress"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = local.labels
}

# SG usado pelo VPC Connector do App Runner (egress liberado)
resource "aws_security_group" "apprunner_connector_sg" {
  name        = "${var.name_prefix}-apprunner-connector-sg"
  description = "SG do VPC Connector do App Runner"
  vpc_id      = data.aws_vpc.default.id
  tags        = local.labels

  egress {
    description = "All egress"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Permite MySQL (3306) do SG do VPC Connector -> SG do RDS
resource "aws_security_group_rule" "mysql_from_apprunner" {
  type                     = "ingress"
  description              = "Permite MySQL (3306) a partir do VPC Connector do App Runner"
  from_port                = 3306
  to_port                  = 3306
  protocol                 = "tcp"
  security_group_id        = aws_security_group.mysql.id
  source_security_group_id = aws_security_group.apprunner_connector_sg.id
}

resource "random_password" "rds_appuser" {
  length           = 20
  special          = true
  override_special = "!#$%&()*+,-.:;<=>?[]^_{|}~"
}

resource "aws_db_instance" "mysql" {
  identifier             = "${var.name_prefix}-mysql"
  engine                 = "mysql"
  engine_version         = "8.0"
  instance_class         = var.rds_instance_class
  allocated_storage      = var.rds_allocated_storage
  db_name                = var.rds_db_name
  username               = var.rds_username
  password               = random_password.rds_appuser.result
  db_subnet_group_name   = aws_db_subnet_group.this.name
  vpc_security_group_ids = [aws_security_group.mysql.id]
  publicly_accessible    = var.rds_publicly_accessible
  skip_final_snapshot    = true
  deletion_protection    = false
  tags                   = local.labels
}

# =========================
# Secrets Manager (sensíveis)
# =========================
resource "aws_secretsmanager_secret" "url_bd" {
  name                          = "/${var.name_prefix}/stg/URL_BD"
  tags                          = local.labels
  force_delete_without_recovery = true
}

resource "aws_secretsmanager_secret" "user_bd" {
  name                          = "/${var.name_prefix}/stg/USER_BD"
  tags                          = local.labels
  force_delete_without_recovery = true
}

resource "aws_secretsmanager_secret" "password_bd" {
  name                          = "/${var.name_prefix}/stg/PASSWORD_BD"
  tags                          = local.labels
  force_delete_without_recovery = true
}

resource "aws_secretsmanager_secret" "database_url" {
  name                          = "/${var.name_prefix}/stg/DATABASE_URL"
  tags                          = local.labels
  force_delete_without_recovery = true
}

resource "aws_secretsmanager_secret" "openai_api_key" {
  name                          = "/${var.name_prefix}/stg/OPENAI_API_KEY"
  tags                          = local.labels
  force_delete_without_recovery = true
}

resource "aws_secretsmanager_secret" "app_crm_acess_token" {
  name                          = "/${var.name_prefix}/stg/APP_CRM_ACESS_TOKEN"
  tags                          = local.labels
  force_delete_without_recovery = true
}

resource "aws_secretsmanager_secret" "api_principal_api_key" {
  name                          = "/${var.name_prefix}/stg/API_PRINCIPAL_API_KEY"
  tags                          = local.labels
  force_delete_without_recovery = true
}

resource "aws_secretsmanager_secret" "api_principal_secret_key" {
  name                          = "/${var.name_prefix}/stg/API_PRINCIPAL_SECRET_KEY"
  tags                          = local.labels
  force_delete_without_recovery = true
}

resource "aws_secretsmanager_secret" "whatsapp_client_token" {
  name                          = "/${var.name_prefix}/stg/WHASTAPP_CLIENT_TOKEN"
  tags                          = local.labels
  force_delete_without_recovery = true
}

resource "aws_secretsmanager_secret" "whatsapp_instance_id" {
  name                          = "/${var.name_prefix}/stg/WHASTAPP_INSTANCE_ID"
  tags                          = local.labels
  force_delete_without_recovery = true
}

resource "aws_secretsmanager_secret" "whatsapp_token" {
  name                          = "/${var.name_prefix}/stg/WHASTAPP_TOKEN"
  tags                          = local.labels
  force_delete_without_recovery = true
}

resource "aws_secretsmanager_secret" "db_app_creds" {
  name                          = "/${var.name_prefix}/stg/db-app-creds"
  tags                          = local.labels
  force_delete_without_recovery = true
}

# -------- Secret Versions --------

resource "aws_secretsmanager_secret_version" "url_bd_v" {
  secret_id     = aws_secretsmanager_secret.url_bd.id
  secret_string = local.jdbc_url
}

resource "aws_secretsmanager_secret_version" "user_bd_v" {
  secret_id     = aws_secretsmanager_secret.user_bd.id
  secret_string = var.rds_username
}

resource "aws_secretsmanager_secret_version" "password_bd_v" {
  secret_id     = aws_secretsmanager_secret.password_bd.id
  secret_string = random_password.rds_appuser.result
}

resource "aws_secretsmanager_secret_version" "database_url_v" {
  secret_id     = aws_secretsmanager_secret.database_url.id
  secret_string = local.sqlalchemy_url
}

resource "aws_secretsmanager_secret_version" "openai_api_key_v" {
  secret_id     = aws_secretsmanager_secret.openai_api_key.id
  secret_string = var.OPENAI_API_KEY
}

resource "aws_secretsmanager_secret_version" "app_crm_acess_token_v" {
  secret_id     = aws_secretsmanager_secret.app_crm_acess_token.id
  secret_string = var.APP_CRM_ACESS_TOKEN
}

resource "aws_secretsmanager_secret_version" "api_principal_api_key_v" {
  secret_id     = aws_secretsmanager_secret.api_principal_api_key.id
  secret_string = var.API_PRINCIPAL_API_KEY
}

resource "aws_secretsmanager_secret_version" "api_principal_secret_key_v" {
  secret_id     = aws_secretsmanager_secret.api_principal_secret_key.id
  secret_string = var.API_PRINCIPAL_SECRET_KEY
}

resource "aws_secretsmanager_secret_version" "whatsapp_client_token_v" {
  secret_id     = aws_secretsmanager_secret.whatsapp_client_token.id
  secret_string = var.WHASTAPP_CLIENT_TOKEN
}

resource "aws_secretsmanager_secret_version" "whatsapp_instance_id_v" {
  secret_id     = aws_secretsmanager_secret.whatsapp_instance_id.id
  secret_string = var.WHASTAPP_INSTANCE_ID
}

resource "aws_secretsmanager_secret_version" "whatsapp_token_v" {
  secret_id     = aws_secretsmanager_secret.whatsapp_token.id
  secret_string = var.WHASTAPP_TOKEN
}


resource "aws_secretsmanager_secret_version" "db_app_creds_v" {
  secret_id     = aws_secretsmanager_secret.db_app_creds.id
  secret_string = jsonencode({
    username = var.rds_username
    password = random_password.rds_appuser.result
    host     = local.db_host
    dbname   = var.rds_db_name
    port     = 3306
    jdbc_url = local.jdbc_url
  })
}

# =========================
# IAM p/ App Runner
# =========================

# --- Access Role (puxar imagem do ECR) ---
data "aws_iam_policy_document" "apprunner_access_trust" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      type        = "Service"
      identifiers = ["build.apprunner.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "apprunner_access_role" {
  name               = "${var.name_prefix}-apprunner-access-role"
  assume_role_policy = data.aws_iam_policy_document.apprunner_access_trust.json
  tags               = local.labels
}

data "aws_iam_policy_document" "apprunner_access_ecr" {
  statement {
    actions   = ["ecr:GetAuthorizationToken"]
    resources = ["*"]
  }
  statement {
    actions = [
      "ecr:BatchCheckLayerAvailability",
      "ecr:GetDownloadUrlForLayer",
      "ecr:BatchGetImage"
    ]
    resources = ["*"]
  }
}

resource "aws_iam_policy" "apprunner_access_ecr" {
  name   = "${var.name_prefix}-apprunner-access-ecr"
  policy = data.aws_iam_policy_document.apprunner_access_ecr.json
}

resource "aws_iam_role_policy_attachment" "apprunner_access_attach" {
  role       = aws_iam_role.apprunner_access_role.name
  policy_arn = aws_iam_policy.apprunner_access_ecr.arn
}

# --- Instance Role (runtime: Secrets, SQS, Logs) ---
data "aws_iam_policy_document" "apprunner_instance_trust" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      type        = "Service"
      identifiers = [
        "tasks.apprunner.amazonaws.com",
        "apprunner.amazonaws.com"
      ]
    }
  }
}

resource "aws_iam_role" "apprunner_instance_role" {
  name               = "${var.name_prefix}-apprunner-instance-role"
  assume_role_policy = data.aws_iam_policy_document.apprunner_instance_trust.json
  tags               = local.labels
}

# Política de runtime (SQS + Secrets + Logs + ECR pulls se necessário)
data "aws_iam_policy_document" "apprunner_policy" {
  statement {
    actions   = ["sqs:SendMessage", "sqs:ReceiveMessage", "sqs:DeleteMessage", "sqs:GetQueueAttributes", "sqs:GetQueueUrl"]
    resources = [aws_sqs_queue.fifo.arn]
  }

  statement {
    actions = ["secretsmanager:GetSecretValue", "secretsmanager:DescribeSecret"]
    resources = [
      aws_secretsmanager_secret.url_bd.arn,
      aws_secretsmanager_secret.user_bd.arn,
      aws_secretsmanager_secret.password_bd.arn,
      aws_secretsmanager_secret.database_url.arn,
      aws_secretsmanager_secret.openai_api_key.arn,
      aws_secretsmanager_secret.app_crm_acess_token.arn,
      aws_secretsmanager_secret.api_principal_api_key.arn,
      aws_secretsmanager_secret.api_principal_secret_key.arn,
      aws_secretsmanager_secret.whatsapp_client_token.arn,
      aws_secretsmanager_secret.whatsapp_instance_id.arn,
      aws_secretsmanager_secret.whatsapp_token.arn,
      aws_secretsmanager_secret.db_app_creds.arn
    ]
  }

  statement {
    actions   = ["logs:*"]
    resources = ["*"]
  }

  # (Opcional) pulls no ECR durante runtime (normalmente não necessário)
  statement {
    actions = [
      "ecr:GetAuthorizationToken",
      "ecr:BatchCheckLayerAvailability",
      "ecr:GetDownloadUrlForLayer",
      "ecr:BatchGetImage"
    ]
    resources = ["*"]
  }
}

resource "aws_iam_policy" "apprunner_policy" {
  name   = "${var.name_prefix}-apprunner-policy"
  policy = data.aws_iam_policy_document.apprunner_policy.json
}

resource "aws_iam_role_policy_attachment" "apprunner_instance_attach" {
  role       = aws_iam_role.apprunner_instance_role.name
  policy_arn = aws_iam_policy.apprunner_policy.arn
}

# =========================
# VPC Connector do App Runner
# =========================
resource "aws_apprunner_vpc_connector" "this" {
  vpc_connector_name = "${var.name_prefix}-apprunner-vpc"
  subnets            = local.apprunner_subnet_ids
  security_groups    = [aws_security_group.apprunner_connector_sg.id]
  tags               = local.labels

  lifecycle {
    precondition {
      condition     = length(local.apprunner_subnet_ids) > 0
      error_message = "Nenhuma subnet válida para o VPC Connector (ajuste apprunner_az_blocklist/apprunner_subnet_id_blocklist)."
    }
  }
}

# =========================
# App Runner: 3 serviços
# =========================

# 1) API Intermediária (Java/Spring)
resource "aws_apprunner_service" "api_intermediaria" {
  count = var.create_services ? 1 : 0

  depends_on = [
    aws_secretsmanager_secret_version.url_bd_v,
    aws_secretsmanager_secret_version.user_bd_v,
    aws_secretsmanager_secret_version.password_bd_v,
    aws_iam_role_policy_attachment.apprunner_instance_attach,
    aws_iam_role_policy_attachment.apprunner_access_attach
  ]

  service_name = "${var.name_prefix}-api-intermediaria"

  source_configuration {
    image_repository {
      image_identifier      = "${var.account_id}.dkr.ecr.${var.region}.amazonaws.com/${var.ecr_repository}:${var.image_tag_api_intermediaria}"
      image_repository_type = "ECR"

      image_configuration {
        port = tostring(var.api_intermediaria_port)

        runtime_environment_variables = {
          AWS_SQS_URL            = aws_sqs_queue.fifo.url
          SPRING_PROFILES_ACTIVE = "prod"
        }

        runtime_environment_secrets = {
          URL_BD      = aws_secretsmanager_secret.url_bd.arn
          USER_BD     = aws_secretsmanager_secret.user_bd.arn
          PASSWORD_BD = aws_secretsmanager_secret.password_bd.arn
        }
      }
    }

    authentication_configuration {
      access_role_arn = aws_iam_role.apprunner_access_role.arn
    }

    auto_deployments_enabled = true
  }

  instance_configuration {
    instance_role_arn = aws_iam_role.apprunner_instance_role.arn
  }

  health_check_configuration {
    path                 = "/actuator/health"
    interval             = 10
    timeout              = 5
    healthy_threshold    = 1
    unhealthy_threshold  = 5
  }

  network_configuration {
    egress_configuration {
      egress_type       = "VPC"
      vpc_connector_arn = aws_apprunner_vpc_connector.this.arn
    }
  }

  tags = local.labels
}

# 2) API do Agente (Python/FastAPI)
resource "aws_apprunner_service" "api_agente" {
  count = var.create_services ? 1 : 0

  depends_on = [
    aws_secretsmanager_secret_version.database_url_v,
    aws_secretsmanager_secret_version.openai_api_key_v,
    aws_iam_role_policy_attachment.apprunner_instance_attach,
    aws_iam_role_policy_attachment.apprunner_access_attach
  ]

  service_name = "${var.name_prefix}-api-agente"

  source_configuration {
    image_repository {
      image_identifier      = "${var.account_id}.dkr.ecr.${var.region}.amazonaws.com/${var.ecr_repository}:${var.image_tag_api_agente}"
      image_repository_type = "ECR"

      image_configuration {
        port = tostring(var.api_agente_port)

        runtime_environment_secrets = {
          DATABASE_URL   = aws_secretsmanager_secret.database_url.arn
          OPENAI_API_KEY = aws_secretsmanager_secret.openai_api_key.arn
        }
      }
    }

    authentication_configuration {
      access_role_arn = aws_iam_role.apprunner_access_role.arn
    }

    auto_deployments_enabled = true
  }

  instance_configuration {
    instance_role_arn = aws_iam_role.apprunner_instance_role.arn
  }

  health_check_configuration {
    path                 = "/health"
    interval             = 10
    timeout              = 5
    healthy_threshold    = 1
    unhealthy_threshold  = 5
  }

  network_configuration {
    egress_configuration {
      egress_type       = "VPC"
      vpc_connector_arn = aws_apprunner_vpc_connector.this.arn
    }
  }

  tags = local.labels
}

# 3) API Principal (Java/Spring)
resource "aws_apprunner_service" "api_principal" {
  count = var.create_services ? 1 : 0

  depends_on = [
    aws_secretsmanager_secret_version.url_bd_v,
    aws_secretsmanager_secret_version.user_bd_v,
    aws_secretsmanager_secret_version.password_bd_v,
    aws_secretsmanager_secret_version.app_crm_acess_token_v,
    aws_secretsmanager_secret_version.api_principal_api_key_v,
    aws_secretsmanager_secret_version.api_principal_secret_key_v,
    aws_secretsmanager_secret_version.whatsapp_client_token_v,
    aws_secretsmanager_secret_version.whatsapp_instance_id_v,
    aws_secretsmanager_secret_version.whatsapp_token_v,
    aws_iam_role_policy_attachment.apprunner_instance_attach,
    aws_iam_role_policy_attachment.apprunner_access_attach
  ]

  service_name = "${var.name_prefix}-api-principal"

  source_configuration {
    image_repository {
      image_identifier      = "${var.account_id}.dkr.ecr.${var.region}.amazonaws.com/${var.ecr_repository}:${var.image_tag_api_principal}"
      image_repository_type = "ECR"

      image_configuration {
        port = tostring(var.api_principal_port)

        runtime_environment_variables = {
          AGENTE_URL = aws_apprunner_service.api_agente[0].service_url
          APP_CRM_URL            = var.APP_CRM_URL
          AWS_SQS_URL            = aws_sqs_queue.fifo.url
          SPRING_PROFILES_ACTIVE = "prod"
        }

        runtime_environment_secrets = {
          PASSWORD_BD              = aws_secretsmanager_secret.password_bd.arn
          URL_BD                   = aws_secretsmanager_secret.url_bd.arn
          USER_BD                  = aws_secretsmanager_secret.user_bd.arn
          API_PRINCIPAL_API_KEY    = aws_secretsmanager_secret.api_principal_api_key.arn
          API_PRINCIPAL_SECRET_KEY = aws_secretsmanager_secret.api_principal_secret_key.arn
          WHASTAPP_CLIENT_TOKEN    = aws_secretsmanager_secret.whatsapp_client_token.arn
          WHASTAPP_INSTANCE_ID     = aws_secretsmanager_secret.whatsapp_instance_id.arn
          WHASTAPP_TOKEN           = aws_secretsmanager_secret.whatsapp_token.arn
          APP_CRM_ACESS_TOKEN      = aws_secretsmanager_secret.app_crm_acess_token.arn
        }
      }
    }

    authentication_configuration {
      access_role_arn = aws_iam_role.apprunner_access_role.arn
    }

    auto_deployments_enabled = true
  }

  instance_configuration {
    instance_role_arn = aws_iam_role.apprunner_instance_role.arn
  }

  health_check_configuration {
    path                 = "/actuator/health"
    interval             = 10
    timeout              = 5
    healthy_threshold    = 1
    unhealthy_threshold  = 5
  }

  network_configuration {
    egress_configuration {
      egress_type       = "VPC"
      vpc_connector_arn = aws_apprunner_vpc_connector.this.arn
    }
  }

  tags = local.labels
}
