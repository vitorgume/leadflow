package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.mensagem;

import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebClientExecutorTest {

    @Mock(answer = Answers.RETURNS_SELF)
    WebClient.Builder webClientBuilder;

    @Mock WebClient webClient;
    @Mock WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock WebClient.RequestBodySpec requestBodySpec;
    @Mock WebClient.RequestHeadersSpec requestHeadersSpec; // Removido o wildcard <?> para facilitar o mock
    @Mock WebClient.ResponseSpec responseSpec;

    WebClientExecutor executor;
    RetryBackoffSpec retrySpec;

    @BeforeEach
    void setup() {
        // Configura o Builder para retornar o mock do WebClient
        when(webClientBuilder.build()).thenReturn(webClient);

        retrySpec = Retry.fixedDelay(3, Duration.ofMillis(10)); // Duration menor para testes rápidos
        executor = new WebClientExecutor(webClientBuilder, retrySpec);
    }

    @Test
    void deveExecutarPostComSucesso() {
        String uri = "http://api.test/send";
        Object payload = Map.of("k", "v");
        Map<String, String> headers = Map.of("h", "v");
        String errorMsg = "err-msg";

        // Mocks da cadeia
        when(webClient.method(HttpMethod.POST)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodySpec);

        // Mock do contentType (agora é chamado explicitamente no código)
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);

        // Mock do header customizado
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);

        // Mock do body
        doReturn(requestHeadersSpec).when(requestBodySpec).bodyValue(payload);

        // Mock do retrieve e response
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("OK"));

        // Act
        String result = executor.execute(uri, payload, headers, errorMsg, HttpMethod.POST);

        // Assert
        assertEquals("OK", result);

        verify(webClient).method(HttpMethod.POST);
        verify(requestBodyUriSpec).uri(uri);
        verify(requestBodySpec).contentType(MediaType.APPLICATION_JSON);
        verify(requestBodySpec).header("h", "v"); // Verifica o header específico
        verify(requestBodySpec).bodyValue(payload);
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec).bodyToMono(String.class);
    }

    @Test
    void deveExecutarGetComSucessoSemBody() {
        String uri = "http://api.test/status";
        Map<String, String> headers = Map.of();
        String err = "err";

        // GET retorna RequestBodyUriSpec, que vira RequestBodySpec no código refatorado
        when(webClient.method(HttpMethod.GET)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodySpec);

        // contentType também é setado no GET pelo código refatorado
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);

        // Sem body, o código usa o próprio spec como RequestHeadersSpec
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("OKGET"));

        String out = executor.execute(uri, null, headers, err, HttpMethod.GET);

        assertEquals("OKGET", out);

        verify(webClient).method(HttpMethod.GET);
        verify(requestBodyUriSpec).uri(uri);
        verify(requestBodySpec).contentType(MediaType.APPLICATION_JSON);
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec).bodyToMono(String.class);
    }

    @Test
    void deveExecutarPutComBodyEHeaders() {
        String uri = "http://api.test/update";
        Object body = Map.of("a", "b");
        Map<String, String> headers = Map.of("h1", "v1", "h2", "v2");
        String err = "err";

        when(webClient.method(HttpMethod.PUT)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);

        // Lenient porque chamamos header várias vezes e a ordem do Map pode variar
        lenient().when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);

        doReturn(requestHeadersSpec).when(requestBodySpec).bodyValue(body);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("OKPUT"));

        String out = executor.execute(uri, body, headers, err, HttpMethod.PUT);
        assertEquals("OKPUT", out);

        // Verifica se os headers foram adicionados
        verify(requestBodySpec).header("h1", "v1");
        verify(requestBodySpec).header("h2", "v2");

        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec).bodyToMono(String.class);
    }

    @Test
    void deveLancarDataProviderExceptionQuandoOnStatusDetectaErroHttp() {
        String uri = "http://api.test/fail";
        Object payload = Map.of("x", 1);
        String errMsg = "failure";

        // Configura retry que falha na hora
        RetryBackoffSpec noRetry = Retry.fixedDelay(1, Duration.ZERO).filter(ex -> false);
        executor = new WebClientExecutor(webClientBuilder, noRetry); // Passa o builder aqui

        when(webClient.method(HttpMethod.POST)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        doReturn(requestHeadersSpec).when(requestBodySpec).bodyValue(payload);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        // Simula falha tratada pelo onStatus
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.error(new DataProviderException(
                        "failure | HTTP 400 | Body: {\"error\":\"bad request\"}", null)));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> executor.execute(uri, payload, Map.of(), errMsg, HttpMethod.POST)
        );

        assertTrue(ex.getMessage().contains("HTTP 400"));
        assertTrue(ex.getMessage().contains("Body"));
    }

    @Test
    void deveLancarDataProviderExceptionQuandoFalhaAntesDoRetrieve() {
        String uri = "http://api.test/fail-sync";
        String err = "sync-failure";

        when(webClient.method(HttpMethod.DELETE)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenThrow(new RuntimeException("boom"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> executor.execute(uri, null, Map.of(), err, HttpMethod.DELETE)
        );

        assertTrue(ex.getMessage().startsWith(err));
        assertTrue(ex.getMessage().contains("cause=boom"));
        verify(webClient).method(HttpMethod.DELETE);
    }

    @Test
    void deveRetentarQuandoFalharDuasVezesESucessoNaTerceira() {
        String uri = "http://api.test/retry";
        Object body = "x";
        String err = "err";

        when(webClient.method(HttpMethod.POST)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        doReturn(requestHeadersSpec).when(requestBodySpec).bodyValue(body);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        AtomicInteger subs = new AtomicInteger(0);
        Mono<String> flaky = Mono.defer(() -> {
            int n = subs.incrementAndGet();
            if (n < 3) return Mono.error(new RuntimeException("fail-" + n));
            return Mono.just("OK");
        });
        when(responseSpec.bodyToMono(String.class)).thenReturn(flaky);

        String out = executor.execute(uri, body, Map.of(), err, HttpMethod.POST);

        assertEquals("OK", out);
        assertEquals(3, subs.get(), "deveria ter 3 tentativas (1 inicial + 2 retries)");
    }

    @Test
    void onStatus_devePropagarDataProviderException_comBodyDeErro() {
        // Arrange: ExchangeFunction real para simular resposta HTTP
        ExchangeFunction exchange = request -> {
            ClientResponse resp = ClientResponse
                    .create(HttpStatus.BAD_REQUEST)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body("{\"error\":\"bad request\"}")
                    .build();
            return Mono.just(resp);
        };

        // Cria um Builder real com a função de troca simulada
        WebClient.Builder builderReal = WebClient.builder().exchangeFunction(exchange);

        RetryBackoffSpec noRetry = Retry.fixedDelay(1, Duration.ZERO).filter(ex -> false);
        WebClientExecutor executorReal = new WebClientExecutor(builderReal, noRetry);

        // Act + Assert
        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> executorReal.execute(
                        "http://api.test/fake",
                        Map.of("x", 1),
                        Map.of("h", "v"),
                        "failure",
                        HttpMethod.POST)
        );

        assertTrue(ex.getMessage().contains("failure | HTTP 400 | Body: {\"error\":\"bad request\"}"),
                "Mensagem deve conter o texto formatado pelo handler do onStatus");
    }

    @Test
    void onStatus_devePropagarDataProviderException_comBodyVazio_usandoDefaultIfEmpty() {
        ExchangeFunction exchange = request -> {
            ClientResponse resp = ClientResponse
                    .create(HttpStatus.NOT_FOUND)
                    .build(); // Sem body
            return Mono.just(resp);
        };

        WebClient.Builder builderReal = WebClient.builder().exchangeFunction(exchange);
        RetryBackoffSpec noRetry = Retry.fixedDelay(1, Duration.ZERO).filter(ex -> false);
        WebClientExecutor executorReal = new WebClientExecutor(builderReal, noRetry);

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> executorReal.execute(
                        "http://api.test/notfound",
                        null,
                        Map.of(),
                        "not-found",
                        HttpMethod.GET)
        );

        assertTrue(ex.getMessage().contains("not-found | HTTP 404 | Body: <empty-body>"),
                "Mensagem deve indicar body vazio tratado com <empty-body>");
    }
}