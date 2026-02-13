package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.kommo.PayloadKommo;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.crm.IntegracaoKommoDataProvider;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactsResponse;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntegracaoKommoDataProviderTest {

    // --- Mocks Principais ---
    @Mock
    private WebClient webClient;

    // O segredo: RETURNS_SELF evita NullPointerException no construtor
    @Mock(answer = Answers.RETURNS_SELF)
    private WebClient.Builder webClientBuilder;

    @Mock
    private RetryBackoffSpec retrySpec;

    // --- Mocks da Cadeia Reativa (Fluent API) ---
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

    // Classe sob teste (instanciada manualmente)
    private IntegracaoKommoDataProvider provider;

    // Constantes de teste
    private final String TOKEN = "bearer-token-123";
    private final String TELEFONE = "5511999999999";
    private final String CRM_URL = "http://crm-cliente.com";

    @BeforeEach
    void setup() {
        // 1. Configura o fim do Builder para retornar o nosso Mock de WebClient
        when(webClientBuilder.build()).thenReturn(webClient);

        // 2. Mockamos o Retry para retornar ele mesmo quando chamado (caso use .retryWhen)
        // Isso previne NPE se o Reactor tentar acessar specs internos
        retrySpec = Retry.fixedDelay(1, java.time.Duration.ofMillis(10));

        // 3. Instanciação manual APÓS as configurações acima
        provider = new IntegracaoKommoDataProvider(webClientBuilder, retrySpec);
    }

    // ==================================================================================
    // TESTES DE CONSULTA (GET)
    // ==================================================================================

    @Test
    @DisplayName("ConsultaLead: Deve normalizar telefone e retornar ID do lead mais recente")
    void deveRetornarIdLeadQuandoEncontrado() {
        // Arrange
        ContactDto contatoAntigo = criarContatoMock(100, 1000L);
        ContactDto contatoNovo = criarContatoMock(200, 2000L); // Esse deve ser escolhido (maior data)

        // Cenário de robustez: Contato com dados nulos
        ContactDto contatoBugado = mock(ContactDto.class);
        lenient().when(contatoBugado.getEmbedded()).thenReturn(null);

        ContactsResponse.Embedded embedded = new ContactsResponse.Embedded();
        embedded.setContacts(List.of(contatoAntigo, contatoNovo, contatoBugado));
        ContactsResponse responseApi = new ContactsResponse(embedded);

        mockWebClientGetChain(responseApi);

        // Act
        Optional<Integer> result = provider.consultaLeadPeloTelefone(TELEFONE, TOKEN, CRM_URL);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(200, result.get());
    }

    @Test
    @DisplayName("ConsultaLead: Deve normalizar telefone null para vazio e adicionar +")
    void deveTratarTelefoneNull() {
        // Arrange
        // Simula resposta vazia da API (sem contatos)
        mockWebClientGetChain(new ContactsResponse(null));

        // Act
        provider.consultaLeadPeloTelefone(null, TOKEN, CRM_URL);

        // Assert
        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        verify(requestHeadersUriSpec).uri(uriCaptor.capture());

        // Verifica se a URL foi construída corretamente com o "+" encoded (%2B)
        assertTrue(uriCaptor.getValue().getRawQuery().contains("query=+"));
    }

    @Test
    @DisplayName("ConsultaLead: Não deve adicionar + se já existir")
    void deveManterTelefoneComMais() {
        // Arrange
        mockWebClientGetChain(new ContactsResponse(null));

        // Act
        provider.consultaLeadPeloTelefone("+5511988887777", TOKEN, CRM_URL);

        // Assert
        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        verify(requestHeadersUriSpec).uri(uriCaptor.capture());
        // Deve conter o número exato, com o + encoded
        assertTrue(uriCaptor.getValue().getQuery().contains("query=+5511988887777"));
    }

    @Test
    @DisplayName("ConsultaLead: Deve retornar vazio quando API retorna Mono.empty (ex: 404 tratado)")
    void deveRetornarVazioQuandoApiRetornaVazio() {
        // Arrange
        mockWebClientGetChain(null); // Passar null força o mock a retornar Mono.empty()

        // Act
        Optional<Integer> result = provider.consultaLeadPeloTelefone(TELEFONE, TOKEN, CRM_URL);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("ConsultaLead: Deve retornar vazio quando lista de contatos for null")
    void deveRetornarVazioQuandoListaContatosNull() {
        // Arrange
        ContactsResponse.Embedded embedded = mock(ContactsResponse.Embedded.class);
        when(embedded.getContacts()).thenReturn(null);
        ContactsResponse response = new ContactsResponse(embedded);

        mockWebClientGetChain(response);

        // Act
        Optional<Integer> result = provider.consultaLeadPeloTelefone(TELEFONE, TOKEN, CRM_URL);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("ConsultaLead: Deve ignorar contatos que não possuem leads")
    void deveIgnorarContatosSemLeadsValidos() {
        // Arrange
        // C1: Embedded existe, mas leads é null
        ContactDto c1 = mock(ContactDto.class);
        ContactDto.Embedded e1 = mock(ContactDto.Embedded.class);
        when(c1.getEmbedded()).thenReturn(e1);
        when(e1.getLeads()).thenReturn(null);

        // C2: Leads é lista vazia
        ContactDto c2 = mock(ContactDto.class);
        ContactDto.Embedded e2 = mock(ContactDto.Embedded.class);
        when(c2.getEmbedded()).thenReturn(e2);
        when(e2.getLeads()).thenReturn(Collections.emptyList());

        ContactsResponse.Embedded embedded = new ContactsResponse.Embedded();
        embedded.setContacts(List.of(c1, c2));
        ContactsResponse response = new ContactsResponse(embedded);

        mockWebClientGetChain(response);

        // Act
        Optional<Integer> result = provider.consultaLeadPeloTelefone(TELEFONE, TOKEN, CRM_URL);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("ConsultaLead: Deve lançar DataProviderException ao ocorrer erro de rede")
    void deveLancarExcecaoNaConsulta() {
        // Arrange - Quebra a cadeia no .retrieve()
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenThrow(new RuntimeException("Timeout Connection"));

        // Act & Assert
        assertThrows(DataProviderException.class,
                () -> provider.consultaLeadPeloTelefone(TELEFONE, TOKEN, CRM_URL));
    }

    // ==================================================================================
    // TESTES DE ATUALIZAÇÃO (PATCH)
    // ==================================================================================

    @Test
    @DisplayName("AtualizarCard: Deve executar PATCH com sucesso e montar URI correta")
    void deveAtualizarCardComSucesso() {
        // Arrange
        Integer idLead = 123;
        PayloadKommo payload = PayloadKommo.builder().statusId(1).build();

        mockWebClientPatchChain();

        // Act
        assertDoesNotThrow(() -> provider.atualizarCard(payload, idLead, TOKEN, CRM_URL));

        // Assert
        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        verify(requestBodyUriSpec).uri(uriCaptor.capture());

        // Verifica se a URL base foi respeitada e o ID expandido
        URI uriUsada = uriCaptor.getValue();
        assertEquals("crm-cliente.com", uriUsada.getHost());
        assertEquals("/leads/123", uriUsada.getPath());
    }

    @Test
    @DisplayName("AtualizarCard: Deve lançar DataProviderException ao falhar")
    void deveLancarExcecaoNaAtualizacao() {
        // Arrange
        when(webClient.patch()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodySpec);
        // Simula erro na hora de enviar o corpo
        when(requestBodySpec.bodyValue(any())).thenThrow(new RuntimeException("Erro de serialização"));

        // Act & Assert
        assertThrows(DataProviderException.class,
                () -> provider.atualizarCard(PayloadKommo.builder().build(), 123, TOKEN, CRM_URL));
    }

    // ==================================================================================
    // HELPERS
    // ==================================================================================

    /**
     * Configura toda a cadeia de mocks para uma requisição GET (Consulta).
     */
    private void mockWebClientGetChain(ContactsResponse response) {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        if (response != null) {
            when(responseSpec.bodyToMono(ContactsResponse.class)).thenReturn(Mono.just(response));
        } else {
            // Simula retorno vazio ou erro tratado que retorna empty
            when(responseSpec.bodyToMono(ContactsResponse.class)).thenReturn(Mono.empty());
        }
    }

    /**
     * Configura toda a cadeia de mocks para uma requisição PATCH (Atualização).
     */
    private void mockWebClientPatchChain() {
        when(webClient.patch()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodySpec);
        // Note: contentType já foi definido no construtor via defaultHeader, então a chamada aqui é direta bodyValue
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());
    }

    /**
     * Cria um DTO de contato mockado para facilitar os testes.
     */
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