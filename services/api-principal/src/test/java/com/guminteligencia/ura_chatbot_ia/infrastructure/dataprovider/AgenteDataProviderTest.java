package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.MensagemAgenteDto;
import com.guminteligencia.ura_chatbot_ia.domain.Qualificacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AgenteDataProviderTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private AgenteDataProvider provider;

    private final String agenteUriApi = "http://agent"; // Valor simulado do @Value
    private final UUID ID_USUARIO = UUID.randomUUID();
    private final UUID ID_CONVERSA = UUID.randomUUID();
    private MensagemAgenteDto dto;

    @BeforeEach
    void setup() {
        // Garante que o provider tenha a URL configurada corretamente
        provider = new AgenteDataProvider(webClient, agenteUriApi);
        dto = MensagemAgenteDto.builder()
                .clienteId(ID_USUARIO.toString())
                .conversaId(ID_CONVERSA.toString())
                .mensagem("Mensagem teste")
                .audiosUrl(new ArrayList<>())
                .imagensUrl(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Deve enviar mensagem com sucesso e retornar Qualificacao")
    void deveEnviarMensagemComSucesso() {
        // Arrange
        String resultadoEsperado = "Mensagem teste, teste mensagem";

        // --- CORREÇÃO: Recriar o Map esperado igual ao código de produção ---
        Map<String, Object> bodyEsperado = Map.of(
                "cliente_id", dto.getClienteId(),
                "conversa_id", dto.getConversaId(),
                "message", dto.getMensagem(),
                "audios_url", dto.getAudiosUrl(),
                "imagens_url", dto.getImagensUrl()
        );

        // Configuração da cadeia do WebClient
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(agenteUriApi + "/chat")).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);

        // --- CORREÇÃO: Usar o MAP no when ---
        when(requestBodySpec.bodyValue(bodyEsperado)).thenReturn(requestHeadersSpec);

        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(resultadoEsperado));

        // Act
        String result = provider.enviarMensagem(dto);

        // Assert
        assertNotNull(result);
        assertEquals(resultadoEsperado, result);

        // Verifica chamadas
        verify(webClient).post();
        verify(requestBodyUriSpec).uri(agenteUriApi + "/chat");
        verify(requestBodySpec).contentType(MediaType.APPLICATION_JSON);

        // --- CORREÇÃO: Verificar com o MAP ---
        verify(requestBodySpec).bodyValue(bodyEsperado);
    }

    @Test
    @DisplayName("Deve lançar exceção ao falhar envio de mensagem")
    void deveLancarExceptionAoEnviarMensagem() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(dto)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.error(new RuntimeException("Erro API")));

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

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(expectedBody)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("JSON_OK"));

        // Act
        String result = provider.enviarJsonTrasformacao(texto, ID_USUARIO);

        // Assert
        assertEquals("JSON_OK", result);

        verify(webClient).post();
        verify(requestBodyUriSpec).uri(uri);
        verify(requestBodySpec).bodyValue(expectedBody);
    }

    @Test
    @DisplayName("Deve lançar exceção ao falhar envio de JSON de transformação")
    void deveLancarExceptionAoEnviarJsonTransformacao() {
        // Arrange
        String texto = "bad text";
        String uri = agenteUriApi + "/chat/json";
        Map<String, String> expectedBody = Map.of(
                "mensagem", texto,
                "id_usuario", ID_USUARIO.toString()
        );

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(expectedBody)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.error(new RuntimeException("failJson")));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> provider.enviarJsonTrasformacao(texto, ID_USUARIO));
    }

    @Test
    @DisplayName("Deve acionar o Retry, passar pelo filtro de log e obter sucesso na segunda tentativa")
    void deveAcionarRetryEObterSucessoAposFalha() {
        // Arrange
        String resultadoEsperado = "Sucesso após retry";

        // Configuração do corpo da requisição (igual ao teste de sucesso)
        Map<String, Object> bodyEsperado = Map.of(
                "cliente_id", dto.getClienteId(),
                "conversa_id", dto.getConversaId(),
                "message", dto.getMensagem(),
                "audios_url", dto.getAudiosUrl(),
                "imagens_url", dto.getImagensUrl()
        );

        // --- O PULO DO GATO ---
        // Criamos um Mono que falha na primeira subscrição e funciona na segunda.
        // Isso força o operador .retryWhen() a agir e executar o .filter()
        java.util.concurrent.atomic.AtomicInteger contadorTentativas = new java.util.concurrent.atomic.AtomicInteger(0);

        Mono<String> monoSimulado = Mono.defer(() -> {
            int tentativaAtual = contadorTentativas.incrementAndGet();
            if (tentativaAtual == 1) {
                // 1ª vez: Lança erro para ativar o Retry e o Log
                return Mono.error(new RuntimeException("Erro temporário de conexão"));
            }
            // 2ª vez: Retorna sucesso
            return Mono.just(resultadoEsperado);
        });

        // Configuração dos Mocks do WebClient
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(agenteUriApi + "/chat")).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(bodyEsperado)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        // Aqui retornamos o nosso Mono "inteligente"
        when(responseSpec.bodyToMono(String.class)).thenReturn(monoSimulado);

        // Act
        // Nota: Esse teste vai demorar pelo menos 2 segundos por causa do Duration.ofSeconds(2) no código original
        String result = provider.enviarMensagem(dto);

        // Assert
        assertNotNull(result);
        assertEquals(resultadoEsperado, result);

        // Verificamos se o contador incrementou 2 vezes (1 falha + 1 sucesso)
        assertEquals(2, contadorTentativas.get());

        // Verifica se o fluxo do WebClient foi montado corretamente
        verify(webClient).post();
        verify(requestBodySpec).bodyValue(bodyEsperado);
    }
}