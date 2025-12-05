package com.guminteligencia.ura_chatbot_ia.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.retry.RetryBackoffSpec;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebClientConfigTest {

    private final WebClientConfig config = new WebClientConfig();

    @Test
    void retrySpecNaoTentaNovamenteQuandoErroEh4xx() {
        AtomicInteger tentativas = new AtomicInteger();
        WebClientResponseException erro400 = new WebClientResponseException(
                "Bad Request",
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
        );

        StepVerifier.withVirtualTime(() -> Mono.defer(() -> {
                    tentativas.incrementAndGet();
                    return Mono.error(erro400);
                }).retryWhen(config.retrySpec()))
                .expectErrorSatisfies(th -> assertEquals(erro400, th))
                .verify();

        assertEquals(1, tentativas.get());
    }

    @Test
    void retrySpecTentaTresVezesParaOutrosErros() {
        AtomicInteger tentativas = new AtomicInteger();
        RetryBackoffSpec retrySpec = config.retrySpec();

        StepVerifier.withVirtualTime(() -> Mono.defer(() -> {
                    if (tentativas.getAndIncrement() < 3) {
                        return Mono.error(new RuntimeException("falha"));
                    }
                    return Mono.just("ok");
                }).retryWhen(retrySpec))
                .thenAwait(Duration.ofSeconds(5))
                .expectNext("ok")
                .verifyComplete();

        assertEquals(4, tentativas.get());
    }
}
