package com.guminteligencia.ura_chatbot_ia.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KommoConfigTest {

    private final KommoConfig config = new KommoConfig();

    @Test
    void deveValidarUrlDoKommo() {
        assertThrows(IllegalArgumentException.class,
                () -> config.kommoWebClient(WebClient.builder(), "sem-protocolo"));
    }

    @Test
    void devePropagarHeadersEMapearErrosComoKommoApiException() {
        AtomicReference<ClientRequest> capturedRequest = new AtomicReference<>();
        ExchangeFunction exchange = request -> {
            capturedRequest.set(request);
            return Mono.just(ClientResponse.create(HttpStatus.BAD_REQUEST)
                    .headers(headers -> headers.add(HttpHeaders.CONTENT_TYPE, "application/json"))
                    .body("falha")
                    .build());
        };

        WebClient webClient = config.kommoWebClient(
                WebClient.builder().exchangeFunction(exchange),
                "https://api.exemplo.com"
        );

        KommoConfig.KommoApiException exception = assertThrows(
                KommoConfig.KommoApiException.class,
                () -> webClient.get().uri("/contatos").retrieve().bodyToMono(String.class).block()
        );

        ClientRequest request = capturedRequest.get();
        assertNotNull(request);
        assertTrue(request.url().toString().startsWith("https://api.exemplo.com/contatos"));
        assertEquals("application/hal+json", request.headers().getFirst(HttpHeaders.ACCEPT));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getMessage().contains("falha"));
    }
}
