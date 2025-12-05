package com.guminteligencia.ura_chatbot_ia.config;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SqsConfigTest {

    @Test
    void deveConstruirClienteComEndpointERegiao() {
        SqsConfig config = new SqsConfig("http://localhost:9324");

        SqsClient client = config.sqsClient();

        Region region = client.serviceClientConfiguration().region();

        assertEquals(Region.US_EAST_1, region);
    }
}
