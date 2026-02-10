package com.guminteligencia.ura_chatbot_ia.config;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ContextoEntity;
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
    public DynamoDbTable<ContextoEntity> contextoTable(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${app.dynamo.contexto-table:${DYNAMO_CONTEXTO_TABLE:contexto_entity_leadflow}}")
            String tableName
    ) {
        return enhancedClient.table(tableName, TableSchema.fromBean(ContextoEntity.class));
    }
}
