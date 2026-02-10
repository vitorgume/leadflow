package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.kommo.PayloadKommo;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactsResponse;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class IntegracaoKommoDataProviderTest {

    @Mock
    private WebClient webClient;

    // Mocks para a cadeia do WebClient
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestBodySpec requestBodySpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private IntegracaoKommoDataProvider provider;

    private final String TOKEN = "bearer-token";
    private final String TELEFONE = "5511999999999";

    @Test
    @DisplayName("ConsultaLead: Deve normalizar telefone e retornar ID do lead mais recente")
    void deveRetornarIdLeadQuandoEncontrado() {
        // Arrange
        ContactDto contatoAntigo = criarContatoMock(100, 1000L);
        ContactDto contatoNovo = criarContatoMock(200, 2000L);

        // Cenário de filtro: Contato com embedded null
        ContactDto contatoSemEmbedded = mock(ContactDto.class);
        when(contatoSemEmbedded.getEmbedded()).thenReturn(null);

        ContactsResponse.Embedded embedded = new ContactsResponse.Embedded();
        embedded.setContacts(List.of(contatoAntigo, contatoNovo, contatoSemEmbedded));
        ContactsResponse responseApi = new ContactsResponse(embedded);

        // --- Mock do UriBuilder para Coverage do Lambda ---
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
        when(uriBuilderMock.queryParam(anyString(), any(Object.class))).thenReturn(uriBuilderMock); // Aceita String e Int
        when(uriBuilderMock.build()).thenReturn(URI.create("http://localhost/contacts"));

        // Chain do WebClient
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);
            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpec;
        });
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ContactsResponse.class)).thenReturn(Mono.just(responseApi));

        // Act
        // Passando telefone sem "+", o código deve adicionar
        Optional<Integer> result = provider.consultaLeadPeloTelefone("5511999999999", TOKEN);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(200, result.get());

        // Validação dos parâmetros passados para o UriBuilder (cobre normalizeE164)
        verify(uriBuilderMock).queryParam("query", "+5511999999999");
        verify(uriBuilderMock).queryParam("with", "leads");
    }

    @Test
    @DisplayName("ConsultaLead: Deve normalizar telefone null para vazio e adicionar +")
    void deveTratarTelefoneNull() {
        // Arrange
        mockWebClientChainGenerico(new ContactsResponse(null)); // Retorno vazio

        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
        when(uriBuilderMock.queryParam(anyString(), Optional.ofNullable(any()))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(URI.create("http://uri"));

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(inv -> {
            Function<UriBuilder, URI> func = inv.getArgument(0);
            func.apply(uriBuilderMock);
            return requestHeadersSpec;
        });

        // Act
        provider.consultaLeadPeloTelefone(null, TOKEN);

        // Assert
        // Null vira "" e depois recebe "+", resultando em "+"
        verify(uriBuilderMock).queryParam("query", "+");
    }

    @Test
    @DisplayName("ConsultaLead: Não deve adicionar + se já existir")
    void deveManterTelefoneComMais() {
        // Arrange
        mockWebClientChainGenerico(new ContactsResponse(null));

        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
        when(uriBuilderMock.queryParam(anyString(), Optional.ofNullable(any()))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(URI.create("http://uri"));

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(inv -> {
            Function<UriBuilder, URI> func = inv.getArgument(0);
            func.apply(uriBuilderMock);
            return requestHeadersSpec;
        });

        // Act
        provider.consultaLeadPeloTelefone("+5511000", TOKEN);

        // Assert
        verify(uriBuilderMock).queryParam("query", "+5511000");
    }

    @Test
    @DisplayName("ConsultaLead: Deve retornar vazio quando API retorna null (Mono.empty)")
    void deveRetornarVazioQuandoApiRetornaNull() {
        // Arrange
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        // Simula o .block() retornando null (Mono vazio)
        when(responseSpec.bodyToMono(ContactsResponse.class)).thenReturn(Mono.empty());

        // Act
        Optional<Integer> result = provider.consultaLeadPeloTelefone(TELEFONE, TOKEN);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("ConsultaLead: Deve retornar vazio quando lista de contatos for null")
    void deveRetornarVazioQuandoListaContatosNull() {
        // Arrange
        ContactsResponse.Embedded embedded = mock(ContactsResponse.Embedded.class);
        when(embedded.getContacts()).thenReturn(null); // Lista null
        ContactsResponse response = new ContactsResponse(embedded);

        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);

        mockWebClientChainGenerico(response);

        // Act
        Optional<Integer> result = provider.consultaLeadPeloTelefone(TELEFONE, TOKEN);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("ConsultaLead: Deve filtrar contatos sem leads ou com lista de leads vazia")
    void deveIgnorarContatosSemLeadsValidos() {
        // Arrange
        // Caso 1: Embedded != null, mas Leads == null
        ContactDto c1 = mock(ContactDto.class);
        ContactDto.Embedded e1 = mock(ContactDto.Embedded.class);
        when(c1.getEmbedded()).thenReturn(e1);
        when(e1.getLeads()).thenReturn(null);

        // Caso 2: Embedded != null, Leads != null, mas Lista Vazia
        ContactDto c2 = mock(ContactDto.class);
        ContactDto.Embedded e2 = mock(ContactDto.Embedded.class);
        when(c2.getEmbedded()).thenReturn(e2);
        when(e2.getLeads()).thenReturn(Collections.emptyList());

        ContactsResponse.Embedded embedded = new ContactsResponse.Embedded();
        embedded.setContacts(List.of(c1, c2));
        ContactsResponse response = new ContactsResponse(embedded);

        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);

        mockWebClientChainGenerico(response);

        // Act
        Optional<Integer> result = provider.consultaLeadPeloTelefone(TELEFONE, TOKEN);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("ConsultaLead: Deve lançar exceção ao falhar")
    void deveLancarExcecaoNaConsulta() {
        RuntimeException erroRede = new RuntimeException("Timeout");
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ContactsResponse.class)).thenReturn(Mono.error(erroRede));

        assertThrows(DataProviderException.class,
                () -> provider.consultaLeadPeloTelefone(TELEFONE, TOKEN));
    }

    @Test
    @DisplayName("AtualizarCard: Deve executar PATCH com sucesso e cobrir URI builder")
    void deveAtualizarCardComSucesso() {
        // Arrange
        Integer idLead = 123;
        PayloadKommo payload = PayloadKommo.builder().statusId(1).build();

        // Mock do UriBuilder para coverage do lambda
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build(any(Object.class))).thenReturn(URI.create("http://localhost/leads/123"));

        when(webClient.patch()).thenReturn(requestBodyUriSpec);
        // Intercepta e executa o lambda do URI
        when(requestBodyUriSpec.uri(any(Function.class))).thenAnswer(inv -> {
            Function<UriBuilder, URI> func = inv.getArgument(0);
            func.apply(uriBuilderMock);
            return requestBodySpec;
        });

        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(payload)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        // Act
        assertDoesNotThrow(() -> provider.atualizarCard(payload, idLead, TOKEN));

        // Assert
        verify(uriBuilderMock).path("/leads/{id}");
        verify(uriBuilderMock).build(idLead);
    }

    @Test
    @DisplayName("AtualizarCard: Deve lançar exceção ao falhar")
    void deveLancarExcecaoNaAtualizacao() {
        when(webClient.patch()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.error(new RuntimeException("Erro")));

        assertThrows(DataProviderException.class,
                () -> provider.atualizarCard(PayloadKommo.builder().build(), 123, TOKEN));
    }

    // --- Helpers ---

    private void mockWebClientChainGenerico(ContactsResponse response) {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ContactsResponse.class)).thenReturn(Mono.just(response));
    }

    private ContactDto criarContatoMock(Integer leadId, Long updatedAt) {
        ContactDto contact = mock(ContactDto.class);
        when(contact.getUpdatedAt()).thenReturn(updatedAt);
        ContactDto.Embedded embedded = mock(ContactDto.Embedded.class);
        when(contact.getEmbedded()).thenReturn(embedded);
        ContactDto.LeadRef leadRef = new ContactDto.LeadRef(leadId);
        when(embedded.getLeads()).thenReturn(List.of(leadRef));
        return contact;
    }
}