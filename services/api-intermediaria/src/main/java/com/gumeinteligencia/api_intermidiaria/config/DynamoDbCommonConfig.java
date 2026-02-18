package com.gumeinteligencia.api_intermidiaria.config;

import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.ContextoEntity;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.OutroContatoEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDbCommonConfig {

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Bean
    public DynamoDbTable<ContextoEntity> contextoTable(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${app.dynamo.contexto-table}") String tableName
    ) {
        return enhancedClient.table(tableName, TableSchema.fromBean(ContextoEntity.class));
    }

    @Bean
    public DynamoDbTable<OutroContatoEntity> outroContatoTable(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${app.dynamo.outro-contato-table}") String tableName
    ) {
        return enhancedClient.table(tableName, TableSchema.fromBean(OutroContatoEntity.class));
    }
}
