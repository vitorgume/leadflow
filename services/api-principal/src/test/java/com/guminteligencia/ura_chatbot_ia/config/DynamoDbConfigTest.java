package com.guminteligencia.ura_chatbot_ia.config;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ContextoEntity;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DynamoDbConfigTest {

    private final DynamoDbConfig config = new DynamoDbConfig();

    @Test
    void deveConstruirClienteComRegiaoInformada() {
        DynamoDbClient client = config.dynamoDbClient("us-west-2");

        Region region = client.serviceClientConfiguration().region();
        assertEquals(Region.US_WEST_2, region);
    }

    @Test
    void deveCriarTabelaDeContextoComNomeConfigurado() {
        DynamoDbEnhancedClient enhancedClient = mock(DynamoDbEnhancedClient.class);
        DynamoDbTable<ContextoEntity> table = mock(DynamoDbTable.class);
        when(enhancedClient.table(eq("contexto-custom"), any(TableSchema.class)))
                .thenReturn(table);

        DynamoDbTable<ContextoEntity> resultado = config.contextoTable(enhancedClient, "contexto-custom");

        assertSame(table, resultado);
        ArgumentCaptor<TableSchema<ContextoEntity>> schemaCaptor = ArgumentCaptor.forClass(TableSchema.class);
        verify(enhancedClient).table(eq("contexto-custom"), schemaCaptor.capture());
        assertNotNull(schemaCaptor.getValue());
    }
}
