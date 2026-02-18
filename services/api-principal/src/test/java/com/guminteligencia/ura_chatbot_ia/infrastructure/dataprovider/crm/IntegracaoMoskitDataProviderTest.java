package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.crm;

import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.moskit.ContatoMoskitDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.moskit.PayloadMoskit;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.moskit.ResponseContatoDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.net.URI;
import java.time.Duration;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IntegracaoMoskitDataProviderTest {

    @Mock(answer = Answers.RETURNS_SELF)
    private WebClient.Builder webClientBuilder;

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

    private RetryBackoffSpec retrySpec;

    private IntegracaoMoskitDataProvider dataProvider;

    private final String CRM_URL = "https://api.moskit.com";
    private final String TOKEN = "token-teste";

    @BeforeEach
    void setup() {
        // Configura o Builder para retornar o WebClient mockado
        when(webClientBuilder.build()).thenReturn(webClient);

        // Mocka as configurações iniciais do builder (headers e filters) para evitar NullPointer no construtor
        when(webClientBuilder.defaultHeader(anyString(), anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.filter(any(ExchangeFilterFunction.class))).thenReturn(webClientBuilder);

        // Retry simples para teste
        retrySpec = Retry.fixedDelay(1, Duration.ofMillis(10));

        dataProvider = new IntegracaoMoskitDataProvider(webClientBuilder, retrySpec);
    }

    @Test
    @DisplayName("criarNegocio: Deve executar com sucesso quando a API responder OK")
    void criarNegocio_Sucesso() {
        // Arrange
        PayloadMoskit payload = new PayloadMoskit();

        mockWebClientChain();

        // Configuração específica para toBodilessEntity (usado no criarNegocio)
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        // Act
        assertDoesNotThrow(() -> dataProvider.criarNegocio(payload, TOKEN, CRM_URL));

        // Assert
        verify(webClient).post();
        verify(requestBodyUriSpec).uri(URI.create(CRM_URL + "/deals"));

        // 1. Verifica se o bodyValue foi chamado no BodySpec
        verify(requestBodySpec).bodyValue(payload);

        // 2. CORREÇÃO: O headers é chamado no objeto que o bodyValue retornou (RequestHeadersSpec)
        verify(requestHeadersSpec).headers(any(Consumer.class));

        verify(responseSpec).toBodilessEntity();
    }

    @Test
    @DisplayName("criarNegocio: Deve lançar DataProviderException quando ocorrer erro")
    void criarNegocio_Erro() {
        // Arrange
        PayloadMoskit payload = new PayloadMoskit();
        RuntimeException erroApi = new RuntimeException("Erro API");

        mockWebClientChain();
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.error(erroApi));

        // Act & Assert
        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> dataProvider.criarNegocio(payload, TOKEN, CRM_URL));

        assertEquals("Erro ao criar negócio no moskit.", ex.getMessage());
        assertEquals(erroApi, ex.getCause());
    }

    @Test
    @DisplayName("criarContato: Deve retornar ID quando a API responder com sucesso")
    void criarContato_Sucesso() {
        // Arrange
        ContatoMoskitDto contatoDto = new ContatoMoskitDto(); // Assumindo existência
        ResponseContatoDto responseDto = new ResponseContatoDto(); // Assumindo existência
        responseDto.setId(12345);

        mockWebClientChain();

        // Configuração específica para bodyToMono (usado no criarContato)
        when(responseSpec.bodyToMono(ResponseContatoDto.class)).thenReturn(Mono.just(responseDto));

        // Act
        Integer resultado = dataProvider.criarContato(contatoDto, TOKEN, CRM_URL);

        // Assert
        assertNotNull(resultado);
        assertEquals(12345, resultado);

        verify(webClient).post();
        verify(requestBodyUriSpec).uri(URI.create(CRM_URL + "/contacts"));
        verify(requestBodySpec).bodyValue(contatoDto);
        verify(responseSpec).bodyToMono(ResponseContatoDto.class);
    }

    @Test
    @DisplayName("criarContato: Deve retornar null se o body da resposta for vazio")
    void criarContato_RetornoVazio() {
        // Arrange
        ContatoMoskitDto contatoDto = new ContatoMoskitDto();

        mockWebClientChain();
        when(responseSpec.bodyToMono(ResponseContatoDto.class)).thenReturn(Mono.empty());

        // Act
        Integer resultado = dataProvider.criarContato(contatoDto, TOKEN, CRM_URL);

        // Assert
        assertNull(resultado);
    }

    @Test
    @DisplayName("criarContato: Deve lançar DataProviderException quando ocorrer erro")
    void criarContato_Erro() {
        // Arrange
        ContatoMoskitDto contatoDto = new ContatoMoskitDto();
        RuntimeException erroApi = new RuntimeException("Erro Grave");

        mockWebClientChain();
        when(responseSpec.bodyToMono(ResponseContatoDto.class)).thenReturn(Mono.error(erroApi));

        // Act & Assert
        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> dataProvider.criarContato(contatoDto, TOKEN, CRM_URL));

        assertEquals("Erro ao criar contato no moskit.", ex.getMessage());
        assertEquals(erroApi, ex.getCause());
    }

    // --- Helper para configurar a cadeia comum do WebClient ---
    private void mockWebClientChain() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any(Consumer.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }
}