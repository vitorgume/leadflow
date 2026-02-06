package com.gumeinteligencia.api_intermidiaria.config;

import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.ContextoEntityLeadflow;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.OutroContatoEntityLeadflow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
@Profile("prod")
public class DynamoDbConfig {

    // 1) Low-level client
    @Bean
    public DynamoDbClient dynamoDbClient(
            // permite sobrescrever a região via env var SPRING_AWS_REGION, se quiser
            @Value("${app.aws.region:us-east-1}") String region
    ) {
        return DynamoDbClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    // 2) Enhanced client
    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    // 3) Tabela: Contexto
    @Bean
    public DynamoDbTable<ContextoEntityLeadflow> contextoTable(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${app.dynamo.contexto-table:${DYNAMO_CONTEXTO_TABLE:contexto_entity_leadflow}}")
            String tableName
    ) {
        System.out.println(">>> [DYNAMO] Tabela de CONTEXTO configurada: " + tableName);
        return enhancedClient.table(tableName, TableSchema.fromBean(ContextoEntityLeadflow.class));
    }

    // 4) Tabela: Outro Contato
    @Bean
    public DynamoDbTable<OutroContatoEntityLeadflow> outroContatoTable(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${app.dynamo.outro-contato-table:${DYNAMO_OUTRO_CONTATO_TABLE:outro_contato_entity_leadflow}}")
            String tableName
    ) {
        System.out.println(">>> [DYNAMO] Tabela de OUTRO_CONTATO configurada: " + tableName);
        return enhancedClient.table(tableName, TableSchema.fromBean(OutroContatoEntityLeadflow.class));
    }
}
