package com.gumeinteligencia.api_intermidiaria.infrastructure.repository;

import com.gumeinteligencia.api_intermidiaria.domain.MensagemContexto;
import com.gumeinteligencia.api_intermidiaria.domain.StatusContexto;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.ContextoEntityLeadflow;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContextoRepositoryTest {

    @Mock
    private DynamoDbTemplate dynamoDbTemplate;

    @InjectMocks
    private ContextoRepository contextoRepository;

    private final UUID id = UUID.fromString("d1aea9b5-4007-459f-8629-b3ea7a22ca6b");
    private final String telefone = "45999999999";

    private ContextoEntityLeadflow contexto;

    @BeforeEach
    void setUp() {
        contexto = ContextoEntityLeadflow.builder()
                .id(id)
                .telefone(telefone)
                .status(StatusContexto.ATIVO)
                .mensagens(List.of(MensagemContexto.builder().mensagem("Ola").build()))
                .build();
    }

    @Test
    void deveSalvarContextoComSucesso() {
        when(dynamoDbTemplate.save(contexto)).thenReturn(contexto);

        ContextoEntityLeadflow salvo = contextoRepository.salvar(contexto);

        verify(dynamoDbTemplate).save(contexto);
        assertEquals(contexto.getId(), salvo.getId());
    }

    @Test
    void deveBuscarPorIdComSucesso() {
        Key key = Key.builder().partitionValue(id.toString()).build();

        when(dynamoDbTemplate.load(eq(key), eq(ContextoEntityLeadflow.class))).thenReturn(contexto);

        Optional<ContextoEntityLeadflow> encontrado = contextoRepository.buscarPorId(id.toString());

        assertTrue(encontrado.isPresent());
        assertEquals(contexto.getTelefone(), encontrado.get().getTelefone());
    }

    @Test
    void deveRetornarVazioAoBuscarPorIdInexistente() {
        Key key = Key.builder().partitionValue("inexistente").build();

        when(dynamoDbTemplate.load(eq(key), eq(ContextoEntityLeadflow.class))).thenReturn(null);

        Optional<ContextoEntityLeadflow> encontrado = contextoRepository.buscarPorId("inexistente");

        assertTrue(encontrado.isEmpty());
    }

    @Test
    void deveBuscarPorTelefoneComSucesso() {
        contexto.setTelefone(telefone);

        Page<ContextoEntityLeadflow> page = Page.create(List.of(contexto), null);
        PageIterable<ContextoEntityLeadflow> iterable = PageIterable.create(() -> List.of(page).iterator());

        when(dynamoDbTemplate.scan(ScanEnhancedRequest.builder().build(), ContextoEntityLeadflow.class)).thenReturn(iterable);

        Optional<ContextoEntityLeadflow> resultado = contextoRepository.buscarPorTelefone(telefone);

        assertTrue(resultado.isPresent());
        assertEquals(telefone, resultado.get().getTelefone());
    }

    @Test
    void deveRetornarVazioAoBuscarTelefoneInexistente() {
        Page<ContextoEntityLeadflow> page = Page.create(List.of(), null);
        PageIterable<ContextoEntityLeadflow> iterable = PageIterable.create(() -> List.of(page).iterator());

        when(dynamoDbTemplate.scan(ScanEnhancedRequest.builder().build(), ContextoEntityLeadflow.class)).thenReturn(iterable);

        Optional<ContextoEntityLeadflow> resultado = contextoRepository.buscarPorTelefone("000000000");

        assertTrue(resultado.isEmpty());
    }
}
