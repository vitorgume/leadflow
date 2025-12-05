package com.guminteligencia.ura_chatbot_ia.config;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DynamoDbConfigDevTest {

    @Test
    void deveConstruirClienteComEndpointLocal() {
        DynamoDbConfigDev config = new DynamoDbConfigDev("http://localhost:4566");

        DynamoDbClient client = config.dynamoDbClient();

        Region region = client.serviceClientConfiguration().region();
        assertEquals(Region.US_EAST_1, region);
    }
}
