package com.gumeinteligencia.api_intermidiaria.infrastructure.repository;

import com.gumeinteligencia.api_intermidiaria.domain.outroContato.TipoContato;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.OutroContatoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutroContatoRepositoryTest {

    @Mock
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;

    @Mock
    private DynamoDbTable<OutroContatoEntity> outroContatoTable;

    private OutroContatoRepository outroContatoRepository;

    private OutroContatoEntity outroContato;

    @BeforeEach
    void setUp() {
        when(dynamoDbEnhancedClient.table(anyString(), any(TableSchema.class))).thenReturn(outroContatoTable);
        outroContatoRepository = new OutroContatoRepository(dynamoDbEnhancedClient);

        outroContato = OutroContatoEntity.builder()
                .id(UUID.randomUUID())
                .nome("Maria")
                .telefone("47999999999")
                .descricao("Contato para urgências")
                .tipoContato(TipoContato.PADRAO)
                .build();
    }

    @Test
    void deveSalvarOutroContatoComSucesso() {
        OutroContatoEntity salvo = outroContatoRepository.salvar(outroContato);

        verify(outroContatoTable).putItem(outroContato);
        assertEquals(outroContato, salvo);
    }

    @Test
    void deveListarTodosOsContatos() {
        Page<OutroContatoEntity> page = Page.create(List.of(outroContato));
        PageIterable<OutroContatoEntity> iterable = PageIterable.create(() -> Collections.singletonList(page).iterator());

        when(outroContatoTable.scan()).thenReturn(iterable);

        List<OutroContatoEntity> resultado = outroContatoRepository.listar();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Maria", resultado.get(0).getNome());
    }

    @Test
    void deveRetornarListaVaziaSeNaoHouverContatos() {
        Page<OutroContatoEntity> page = Page.create(Collections.emptyList());
        PageIterable<OutroContatoEntity> iterable = PageIterable.create(() -> Collections.singletonList(page).iterator());

        when(outroContatoTable.scan()).thenReturn(iterable);

        List<OutroContatoEntity> resultado = outroContatoRepository.listar();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveConsultarPorTelefoneComSucesso() {
        String telefone = "47999999999";
        DynamoDbIndex<OutroContatoEntity> index = mock(DynamoDbIndex.class);
        Page<OutroContatoEntity> page = Page.create(List.of(outroContato));
        PageIterable<OutroContatoEntity> iterable = PageIterable.create(() -> Collections.singletonList(page).iterator());

        when(outroContatoTable.index("TelefoneIndex")).thenReturn(index);
        when(index.query(any(QueryEnhancedRequest.class))).thenReturn(iterable);

        Optional<OutroContatoEntity> resultado = outroContatoRepository.consultarPorTelefone(telefone);

        assertTrue(resultado.isPresent());
        assertEquals(outroContato, resultado.get());
        verify(outroContatoTable).index("TelefoneIndex");
    }

    @Test
    void deveRetornarVazioQuandoConsultarPorTelefoneNaoExistente() {
        String telefone = "47000000000";
        DynamoDbIndex<OutroContatoEntity> index = mock(DynamoDbIndex.class);
        Page<OutroContatoEntity> page = Page.create(Collections.emptyList());
        PageIterable<OutroContatoEntity> iterable = PageIterable.create(() -> Collections.singletonList(page).iterator());

        when(outroContatoTable.index("TelefoneIndex")).thenReturn(index);
        when(index.query(any(QueryEnhancedRequest.class))).thenReturn(iterable);

        Optional<OutroContatoEntity> resultado = outroContatoRepository.consultarPorTelefone(telefone);

        assertFalse(resultado.isPresent());
    }
}
