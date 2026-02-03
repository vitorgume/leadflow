package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.kommo.PayloadKommo;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactsResponse;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntegracaoKommoDataProviderTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient webClient;

    @Mock private WebClient.RequestHeadersUriSpec<?> headersUriSpec;
    @Mock private WebClient.RequestBodyUriSpec bodyUriSpec;
    @Mock private WebClient.RequestHeadersSpec<?> headersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    private IntegracaoKommoDataProvider provider;

    private final ObjectMapper om = new ObjectMapper();
    private final String ACESS_TOKEN_TESTE = "tokenteste123";

    @BeforeEach
    void setUp() {
        provider = new IntegracaoKommoDataProvider(webClient);
    }

    // ------------------------------
    // consultaLeadPeloTelefone
    // ------------------------------

    @Test
    void consultaLeadPeloTelefone_deveRetornarEmptyQuandoSemResultado() {
        doReturn(headersUriSpec)
                .when(webClient)
                .get();

        when(headersUriSpec.uri(any(Function.class))).thenReturn(headersSpec);

        doReturn(headersSpec)
                .when(headersSpec)
                .headers(any());

        when(headersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(eq(ContactsResponse.class))).thenReturn(Mono.empty());

        Optional<Integer> out = provider.consultaLeadPeloTelefone("+5511999999999", ACESS_TOKEN_TESTE);

        assertTrue(out.isEmpty());
    }

    @Test
    void consultaLeadPeloTelefone_deveLancarDataProviderExceptionEmErroHttp() {
        // 1. Configurar a cadeia correta

        // webClient.get() retorna o spec de URI
        doReturn(headersUriSpec).when(webClient).get();

        // .uri() DEVE retornar o spec de Headers (headersSpec)
        // Usamos o cast para evitar erros de generics
        when(headersUriSpec.uri(any(Function.class))).thenReturn((WebClient.RequestHeadersSpec) headersSpec);

        // ADICIONE ISTO: .headers() retorna ele mesmo
        doReturn(headersSpec)
                .when(headersSpec)
                .headers(any());

        // .retrieve() retorna o spec de resposta
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        // .bodyToMono() retorna o erro simulado
        when(responseSpec.bodyToMono(eq(com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactsResponse.class)))
                .thenReturn(Mono.error(new RuntimeException("boom")));

        // 2. Act & Assert
        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.consultaLeadPeloTelefone("+5511999999999", ACESS_TOKEN_TESTE));

        assertEquals("Erro ao consultar lead pelo seu telefone.", ex.getMessage());
    }

    // ------------------------------
    // atualizarCard
    // ------------------------------

    @Test
    void atualizarCard_deveEnviarPatchComSucesso() {
        PayloadKommo body = PayloadKommo.builder().statusId(123).build();

        when(webClient.patch()).thenReturn(bodyUriSpec);

        when(bodyUriSpec.uri(any(Function.class))).thenReturn(bodyUriSpec);

        // 3. Configura Content-Type
        when(bodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodyUriSpec);

        when(bodyUriSpec.bodyValue(any(PayloadKommo.class)))
                .thenReturn((WebClient.RequestHeadersSpec) headersSpec);

        doReturn(headersSpec)
                .when(headersSpec)
                .headers(any());

        when(headersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        provider.atualizarCard(body, 42, ACESS_TOKEN_TESTE);

        verify(webClient).patch();
        verify(bodyUriSpec).uri(any(Function.class));
        verify(bodyUriSpec).contentType(MediaType.APPLICATION_JSON);
        verify(bodyUriSpec).bodyValue(body);
        verify(headersSpec).headers(any());
        verify(responseSpec).toBodilessEntity();
    }

    @Test
    void atualizarCard_deveLancarDataProviderExceptionQuandoFalhar() {
        PayloadKommo body = PayloadKommo.builder().statusId(123).build();

        when(webClient.patch()).thenReturn(bodyUriSpec);

        when(bodyUriSpec.uri(any(Function.class))).thenReturn(bodyUriSpec);

        // 3. Configura Content-Type
        when(bodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodyUriSpec);

        when(bodyUriSpec.bodyValue(any(PayloadKommo.class)))
                .thenReturn((WebClient.RequestHeadersSpec) headersSpec);

        doReturn(headersSpec)
                .when(headersSpec)
                .headers(any());

        when(headersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.toBodilessEntity()).thenReturn(Mono.error(new RuntimeException("patch-fail")));

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.atualizarCard(body, 42, ACESS_TOKEN_TESTE));
        assertEquals("Erro ao atualizar card.", ex.getMessage());
    }
}