package com.gumeinteligencia.api_intermidiaria.infrastructure.dataprovider;

import com.gumeinteligencia.api_intermidiaria.domain.Contexto;
import com.gumeinteligencia.api_intermidiaria.domain.MensagemContexto;
import com.gumeinteligencia.api_intermidiaria.domain.StatusContexto;
import com.gumeinteligencia.api_intermidiaria.infrastructure.exceptions.DataProviderException;
import com.gumeinteligencia.api_intermidiaria.infrastructure.mapper.MensagemContextoListConverter;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.ContextoRepository;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.ContextoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContextoDataProviderTest {

    @Mock
    private ContextoRepository repository;

    @Mock
    private DynamoDbClient dynamoDbClient;

    @InjectMocks
    private ContextoDataProvider dataProvider;

    private ContextoEntity contextoEntity;
    private MensagemContexto mensagemContexto;

    @BeforeEach
    void setUp() {
        mensagemContexto = MensagemContexto.builder()
                .mensagem("Oi")
                .imagemUrl("http://img")
                .audioUrl("http://audio")
                .build();

        contextoEntity = ContextoEntity.builder()
                .id(UUID.randomUUID())
                .telefone("45999999999")
                .telefoneUsuario("4798989898989")
                .status(StatusContexto.ATIVO)
                .mensagens(List.of(mensagemContexto))
                .build();
    }

    @Test
    void deveConsultarPorTelefone() {
        AttributeValue mensagensAttr = new MensagemContextoListConverter().transformFrom(contextoEntity.getMensagens());
        Map<String, AttributeValue> itemMap = Map.of(
                "id", AttributeValue.fromS(contextoEntity.getId().toString()),
                "telefone", AttributeValue.fromS(contextoEntity.getTelefone()),
                "telefoneUsuario", AttributeValue.fromS(contextoEntity.getTelefoneUsuario()),
                "status", AttributeValue.fromS(contextoEntity.getStatus().name()),
                "mensagens", mensagensAttr
        );

        QueryResponse mockResponse = QueryResponse.builder()
                .items(List.of(itemMap))
                .build();

        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(mockResponse);

        Optional<Contexto> resultado = dataProvider.consultarPorTelefone(contextoEntity.getTelefone());

        assertTrue(resultado.isPresent());
        assertEquals(contextoEntity.getTelefone(), resultado.get().getTelefone());
        assertEquals(StatusContexto.ATIVO, resultado.get().getStatus());
        assertEquals(1, resultado.get().getMensagens().size());
        assertEquals(mensagemContexto.getMensagem(), resultado.get().getMensagens().get(0).getMensagem());
        assertEquals(mensagemContexto.getImagemUrl(), resultado.get().getMensagens().get(0).getImagemUrl());
        assertEquals(mensagemContexto.getAudioUrl(), resultado.get().getMensagens().get(0).getAudioUrl());
    }

    @Test
    void deveConsultarPorTelefoneSemStatusEPreencherAtivo() {
        AttributeValue mensagensAttr = new MensagemContextoListConverter().transformFrom(contextoEntity.getMensagens());
        Map<String, AttributeValue> itemMap = Map.of(
                "id", AttributeValue.fromS(contextoEntity.getId().toString()),
                "telefone", AttributeValue.fromS(contextoEntity.getTelefone()),
                "telefoneUsuario", AttributeValue.fromS(contextoEntity.getTelefoneUsuario()),
                "mensagens", mensagensAttr
        );

        QueryResponse mockResponse = QueryResponse.builder()
                .items(List.of(itemMap))
                .build();

        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(mockResponse);

        Optional<Contexto> resultado = dataProvider.consultarPorTelefone(contextoEntity.getTelefone());

        assertTrue(resultado.isPresent());
        assertEquals(StatusContexto.ATIVO, resultado.get().getStatus());
    }

    @Test
    void deveRetornarMensagensVaziasQuandoNaoExistirem() {
        Map<String, AttributeValue> itemMap = Map.of(
                "id", AttributeValue.fromS(contextoEntity.getId().toString()),
                "telefone", AttributeValue.fromS(contextoEntity.getTelefone()),
                "telefoneUsuario", AttributeValue.fromS(contextoEntity.getTelefoneUsuario()),
                "status", AttributeValue.fromS(contextoEntity.getStatus().name())
        );

        QueryResponse mockResponse = QueryResponse.builder()
                .items(List.of(itemMap))
                .build();

        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(mockResponse);

        Optional<Contexto> resultado = dataProvider.consultarPorTelefone(contextoEntity.getTelefone());

        assertTrue(resultado.isPresent());
        assertNotNull(resultado.get().getMensagens());
        assertTrue(resultado.get().getMensagens().isEmpty());
    }

    @Test
    void deveRetornarVazioQuandoNaoEncontrarTelefone() {
        QueryResponse responseVazio = QueryResponse.builder()
                .items(List.of())
                .build();

        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(responseVazio);

        Optional<Contexto> resultado = dataProvider.consultarPorTelefone("000000000");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveRetornarVazioQuandoRespostaNaoTemItemsFlag() {
        QueryResponse responseSemFlag = QueryResponse.builder().build();

        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(responseSemFlag);

        Optional<Contexto> resultado = dataProvider.consultarPorTelefone("000000000");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveLancarExcecaoAoConsultarTelefoneComErro() {
        when(dynamoDbClient.query(any(QueryRequest.class))).thenThrow(new RuntimeException("Erro simulado"));

        DataProviderException ex = assertThrows(DataProviderException.class, () ->
                dataProvider.consultarPorTelefone("erro"));

        assertEquals("Erro ao consultar contexto pelo seu telefone e ativo.", ex.getMessage());
    }

    @Test
    void deveSalvarComSucesso() {
        when(repository.salvar(any())).thenReturn(contextoEntity);

        Contexto salvo = dataProvider.salvar(Contexto.builder()
                .id(contextoEntity.getId())
                .telefone(contextoEntity.getTelefone())
                .mensagens(contextoEntity.getMensagens())
                .status(contextoEntity.getStatus())
                .build());

        assertNotNull(salvo);
        assertEquals(contextoEntity.getTelefone(), salvo.getTelefone());
        assertEquals(contextoEntity.getMensagens(), salvo.getMensagens());
    }

    @Test
    void deveLancarExcecaoAoSalvarContexto() {
        when(repository.salvar(any())).thenThrow(new RuntimeException("Falha ao salvar"));

        assertThrows(DataProviderException.class, () ->
                dataProvider.salvar(Contexto.builder().build()));
    }
}
