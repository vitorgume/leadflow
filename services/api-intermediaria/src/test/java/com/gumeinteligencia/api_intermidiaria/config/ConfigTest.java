package com.gumeinteligencia.api_intermidiaria.config;

import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.ContextoEntity;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.OutroContatoEntity;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.sqs.SqsClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class ConfigTest {

    @Test
    void deveCriarSqsClient() {
        SqsConfig config = new SqsConfig("http://localhost:9324");

        try (SqsClient client = config.sqsClient()) {
            assertNotNull(client);
        }
    }

    @Test
    void deveCriarDynamoDbClientProd() {
        DynamoDbConfig config = new DynamoDbConfig();

        try (DynamoDbClient client = config.dynamoDbClient("us-east-1")) {
            assertNotNull(client);
        }
    }

    @Test
    void deveCriarDynamoDbClientDev() {
        DynamoDbConfigDev config = new DynamoDbConfigDev("http://localhost:8000");

        try (DynamoDbClient client = config.dynamoDbClient()) {
            assertNotNull(client);
        }
    }

    @Test
    void deveCriarEnhancedClientETabelas() {
        DynamoDbClient lowLevel = mock(DynamoDbClient.class);
        DynamoDbConfig config = new DynamoDbConfig();

        DynamoDbEnhancedClient enhancedClient = config.dynamoDbEnhancedClient(lowLevel);
        DynamoDbTable<ContextoEntity> contextoTable = config.contextoTable(enhancedClient, "contexto_entity_leadflow");
        DynamoDbTable<OutroContatoEntity> outroContatoTable = config.outroContatoTable(enhancedClient, "outro_contato_entity_leadflow");

        assertNotNull(enhancedClient);
        assertEquals("contexto_entity_leadflow", contextoTable.tableName());
        assertEquals("outro_contato_entity_leadflow", outroContatoTable.tableName());
    }
}
