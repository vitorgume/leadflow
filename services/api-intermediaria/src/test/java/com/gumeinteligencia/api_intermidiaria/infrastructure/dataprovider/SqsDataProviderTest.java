package com.gumeinteligencia.api_intermidiaria.infrastructure.dataprovider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gumeinteligencia.api_intermidiaria.domain.AvisoContexto;
import com.gumeinteligencia.api_intermidiaria.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SqsDataProviderTest {

    @Mock
    private SqsClient sqsClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MensageriaDataProvider sqsDataProvider;

    @Captor
    private ArgumentCaptor<SendMessageRequest> requestCaptor;

    private AvisoContexto avisoContexto;

    @BeforeEach
    void setUp() {
        avisoContexto = AvisoContexto.builder()
                .id(UUID.randomUUID())
                .idContexto(UUID.randomUUID())
                .build();

        ReflectionTestUtils.setField(sqsDataProvider, "queueUrl", "https://fila-sqs.ficticia");
        ReflectionTestUtils.setField(sqsDataProvider, "delay", 10);
    }

    @Test
    void deveEnviarMensagemParaFilaComSucesso() throws Exception {
        String json = "{\"id\":\"" + avisoContexto.getId() + "\"}";
        when(objectMapper.writeValueAsString(avisoContexto)).thenReturn(json);
        when(sqsClient.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(SendMessageResponse.builder().messageId("abc123").build());

        sqsDataProvider.enviarParaFila(avisoContexto);

        verify(sqsClient).sendMessage(requestCaptor.capture());

        SendMessageRequest enviado = requestCaptor.getValue();
        assertEquals("https://fila-sqs.ficticia", enviado.queueUrl());
        assertEquals(json, enviado.messageBody());
        assertEquals("message-group-" + avisoContexto.getId(), enviado.messageGroupId());
    }

    @Test
    void deveLancarDataProviderExceptionAoFalharNaConversaoJson() throws Exception {
        when(objectMapper.writeValueAsString(avisoContexto))
                .thenThrow(new JsonProcessingException("Erro") {});

        DataProviderException ex = assertThrows(DataProviderException.class, () ->
                sqsDataProvider.enviarParaFila(avisoContexto));

        assertEquals("Erro", ex.getCause().getMessage());
    }
}
