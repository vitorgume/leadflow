package com.gumeinteligencia.api_intermidiaria.config;

import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.ContextoEntity;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.OutroContatoEntity;
import org.junit.jupiter.api.Test;
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
}
