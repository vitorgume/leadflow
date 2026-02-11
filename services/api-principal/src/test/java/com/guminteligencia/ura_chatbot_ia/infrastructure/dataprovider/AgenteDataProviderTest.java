package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.MensagemAgenteDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgenteDataProviderTest {

    @Mock
    private WebClient webClient;

    // Mock do Builder com RETURNS_SELF para suportar encadeamento no construtor
    @Mock(answer = Answers.RETURNS_SELF)
    private WebClient.Builder webClientBuilder;

    // Retry real para testar a lógica de retentativa
    private RetryBackoffSpec retrySpec;

    // Mocks da cadeia do WebClient
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestBodySpec requestBodySpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    private AgenteDataProvider provider;

    private final String agenteUriApi = "http://agent-api.com";
    private final UUID ID_USUARIO = UUID.randomUUID();
    private final UUID ID_CONVERSA = UUID.randomUUID();
    private MensagemAgenteDto dto;

    @BeforeEach
    void setup() {
        // Configura o build() para retornar o mock do WebClient
        when(webClientBuilder.build()).thenReturn(webClient);

        // Configura um Retry real e rápido para os testes
        retrySpec = Retry.fixedDelay(1, Duration.ofMillis(10));

        // Instancia o provider manualmente com as dependências mockadas/reais
        provider = new AgenteDataProvider(webClientBuilder, agenteUriApi, retrySpec);

        dto = MensagemAgenteDto.builder()
                .clienteId(ID_USUARIO.toString())
                .conversaId(ID_CONVERSA.toString())
                .mensagem("Mensagem teste")
                .audiosUrl(new ArrayList<>())
                .imagensUrl(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Deve enviar mensagem com sucesso e retornar String")
    void deveEnviarMensagemComSucesso() {
        // Arrange
        String resultadoEsperado = "Mensagem teste, teste mensagem";

        Map<String, Object> bodyEsperado = Map.of(
                "cliente_id", dto.getClienteId(),
                "conversa_id", dto.getConversaId(),
                "message", dto.getMensagem(),
                "audios_url", dto.getAudiosUrl(),
                "imagens_url", dto.getImagensUrl()
        );

        // Configura a cadeia de chamadas para o POST
        mockWebClientPostChain(bodyEsperado, Mono.just(resultadoEsperado));

        // Act
        String result = provider.enviarMensagem(dto);

        // Assert
        assertNotNull(result);
        assertEquals(resultadoEsperado, result);

        verify(webClient).post();
        verify(requestBodyUriSpec).uri(agenteUriApi + "/chat");
        verify(requestBodySpec).bodyValue(bodyEsperado);
    }

    @Test
    @DisplayName("Deve lançar exceção ao falhar envio de mensagem")
    void deveLancarExceptionAoEnviarMensagem() {
        // Arrange
        mockWebClientPostChain(null, Mono.error(new RuntimeException("Erro API")));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> provider.enviarMensagem(dto));
    }

    @Test
    @DisplayName("Deve enviar JSON de transformação com sucesso")
    void deveEnviarJsonTransformacaoComSucesso() {
        // Arrange
        String texto = "some text";
        String uri = agenteUriApi + "/chat/json";

        Map<String, String> expectedBody = Map.of(
                "mensagem", texto,
                "id_usuario", ID_USUARIO.toString()
        );

        mockWebClientPostChain(expectedBody, Mono.just("JSON_OK"));

        // Act
        String result = provider.enviarJsonTrasformacao(texto, ID_USUARIO);

        // Assert
        assertEquals("JSON_OK", result);
        verify(requestBodyUriSpec).uri(uri);
        verify(requestBodySpec).bodyValue(expectedBody);
    }

    @Test
    @DisplayName("Deve lançar exceção ao falhar envio de JSON de transformação")
    void deveLancarExceptionAoEnviarJsonTransformacao() {
        // Arrange
        mockWebClientPostChain(null, Mono.error(new RuntimeException("Erro API")));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> provider.enviarJsonTrasformacao("bad text", ID_USUARIO));
    }

    @Test
    @DisplayName("Deve acionar o Retry e obter sucesso na segunda tentativa")
    void deveAcionarRetryEObterSucessoAposFalha() {
        // Arrange
        String resultadoEsperado = "Sucesso após retry";
        Map<String, Object> bodyEsperado = Map.of(
                "cliente_id", dto.getClienteId(),
                "conversa_id", dto.getConversaId(),
                "message", dto.getMensagem(),
                "audios_url", dto.getAudiosUrl(),
                "imagens_url", dto.getImagensUrl()
        );

        // Simula falha na primeira tentativa e sucesso na segunda
        AtomicInteger contador = new AtomicInteger(0);
        Mono<String> monoSimulado = Mono.defer(() -> {
            if (contador.incrementAndGet() == 1) {
                return Mono.error(new RuntimeException("Erro temporário"));
            }
            return Mono.just(resultadoEsperado);
        });

        mockWebClientPostChain(bodyEsperado, monoSimulado);

        // Act
        String result = provider.enviarMensagem(dto);

        // Assert
        assertEquals(resultadoEsperado, result);
        assertEquals(2, contador.get()); // Garante que houve retentativa
    }

    // --- Helper para configurar a cadeia de mocks do POST ---
    private void mockWebClientPostChain(Object body, Mono<String> responseMono) {
        when(webClient.post()).thenReturn(requestBodyUriSpec);

        // Pode manter anyString() aqui pois está DENTRO do when()
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);

        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);

        // LÓGICA CORRIGIDA:
        if (body != null) {
            // Se passamos um objeto real (teste de sucesso), usamos ele
            when(requestBodySpec.bodyValue(body)).thenReturn(requestHeadersSpec);
        } else {
            // Se passamos null (teste de erro), usamos o matcher any() AQUI DENTRO
            when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        }

        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(responseMono);
    }
}