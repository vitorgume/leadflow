package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ContextoEntityLeadflow;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContextoRepositoryTest {

    @Mock
    private DynamoDbTemplate dynamoDbTemplate;

    @InjectMocks
    private ContextoRepository repository;

    @Captor
    private ArgumentCaptor<ContextoEntityLeadflow> entityCaptor;

    @Captor
    private ArgumentCaptor<Key> keyCaptor;

    private UUID id;
    private ContextoEntityLeadflow dummyEntity;

    @BeforeEach
    void setup() {
        id = UUID.randomUUID();
        dummyEntity = new ContextoEntityLeadflow();
        dummyEntity.setId(id);
    }

    @Test
    void deveDeletar() {
        repository.deletar(dummyEntity);

        verify(dynamoDbTemplate, times(1)).delete(entityCaptor.capture());
        assertSame(dummyEntity, entityCaptor.getValue());
    }

    @Test
    void deveConsultarPorIdRetornarOptionalEmpty() {
        when(dynamoDbTemplate.load(any(Key.class), eq(ContextoEntityLeadflow.class)))
                .thenReturn(null);

        Optional<ContextoEntityLeadflow> result = repository.consultarPorId(id);

        assertTrue(result.isEmpty());

        Key expectedKey = Key.builder()
                .partitionValue(id.toString())
                .build();
        verify(dynamoDbTemplate).load(eq(expectedKey), eq(ContextoEntityLeadflow.class));
    }

    @Test
    void consultarPorIdDeveRetornarContexto() {
        when(dynamoDbTemplate.load(any(Key.class), eq(ContextoEntityLeadflow.class)))
                .thenReturn(dummyEntity);

        Optional<ContextoEntityLeadflow> result = repository.consultarPorId(id);

        assertTrue(result.isPresent());
        assertSame(dummyEntity, result.get());

        Key expectedKey = Key.builder()
                .partitionValue(id.toString())
                .build();
        verify(dynamoDbTemplate).load(eq(expectedKey), eq(ContextoEntityLeadflow.class));
    }

    @Test
    void consultaPorIdQuandoTemplateLancaException() {
        when(dynamoDbTemplate.load(any(Key.class), eq(ContextoEntityLeadflow.class)))
                .thenThrow(new RuntimeException("Dynamo falhou"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> repository.consultarPorId(id));
        assertEquals("Dynamo falhou", ex.getMessage());
    }

    @Test
    void deletarQuandoTemplateLancaException() {
        doThrow(new IllegalStateException("delete failed"))
                .when(dynamoDbTemplate).delete(dummyEntity);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> repository.deletar(dummyEntity));
        assertEquals("delete failed", ex.getMessage());
    }

}