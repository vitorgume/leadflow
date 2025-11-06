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

output "apprunner_urls" {
  value = {
    api_intermediaria = aws_apprunner_service.api_intermediaria.service_url
    api_agente        = aws_apprunner_service.api_agente.service_url
    api_principal     = aws_apprunner_service.api_principal.service_url
  }
}
