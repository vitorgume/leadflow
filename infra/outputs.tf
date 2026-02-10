output "sqs_url" {
  value = aws_sqs_queue.fifo.url
}

output "dynamodb_tables" {
  value = {
    contexto      = aws_dynamodb_table.contexto.name
    outro_contato = aws_dynamodb_table.outro_contato.name
  }
}

output "rds_mysql_endpoint" {
  value = aws_db_instance.mysql.address
}

output "rds_username" {
  description = "Usuário do banco de dados"
  value       = aws_db_instance.mysql.username
}

output "rds_password" {
  description = "Senha do banco de dados"
  value       = random_password.rds_appuser.result
  sensitive   = true # Importante!
}

output "apprunner_urls" {
  description = "URLs dos serviços App Runner"

  value = var.create_services ? {
    api_intermediaria = aws_apprunner_service.api_intermediaria[0].service_url
    api_agente        = aws_apprunner_service.api_agente[0].service_url
    api_principal     = aws_apprunner_service.api_principal[0].service_url
  } : {}
}
