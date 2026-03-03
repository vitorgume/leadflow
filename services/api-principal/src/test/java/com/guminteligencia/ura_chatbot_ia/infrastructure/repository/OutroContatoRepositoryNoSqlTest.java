package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.OutroContatoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutroContatoRepositoryNoSqlTest {

    @Mock
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;

    @Mock
    private DynamoDbTable<OutroContatoEntity> outroContatoTable;

    @Mock
    private DynamoDbIndex<OutroContatoEntity> usuarioTelefoneIndex;

    @Mock
    private PageIterable<OutroContatoEntity> pageIterable;

    @Mock
    private Page<OutroContatoEntity> page;

    // Não usamos @InjectMocks aqui porque precisamos mockar o .table() antes do construtor rodar
    private OutroContatoRepositoryNoSql repository;

    @Captor
    private ArgumentCaptor<OutroContatoEntity> entityCaptor;

    @Captor
    private ArgumentCaptor<Key> keyCaptor;

    private OutroContatoEntity dummyEntity;
    private UUID id;

    @BeforeEach
    void setup() {
        id = UUID.randomUUID();
        dummyEntity = new OutroContatoEntity();
        dummyEntity.setId(id);
        dummyEntity.setTelefone("11999999999");
        dummyEntity.setIdUsuario(UUID.randomUUID());

        // 1. Ensinamos o Client a devolver a nossa tabela mockada quando o construtor for chamado
        when(dynamoDbEnhancedClient.table(eq("outro_contato_entity"), any(TableSchema.class)))
                .thenReturn(outroContatoTable);

        // 2. Instanciamos o repositório manualmente injetando o nosso Client mockado
        repository = new OutroContatoRepositoryNoSql(dynamoDbEnhancedClient);
    }

    @Test
    void deveSalvarComSucesso() {
        repository.salvar(dummyEntity);

        // Verifica se a tabela chamou o putItem passando exatamente a nossa entidade
        verify(outroContatoTable, times(1)).putItem(entityCaptor.capture());
        assertSame(dummyEntity, entityCaptor.getValue());
    }

    @Test
    void deveDeletarComSucesso() {
        repository.deletar(id);

        // Verifica se chamou o deleteItem da tabela
        verify(outroContatoTable, times(1)).deleteItem(keyCaptor.capture());

        // Verifica se a chave gerada usou o ID correto
        Key capturedKey = keyCaptor.getValue();
        assertEquals(id.toString(), capturedKey.partitionKeyValue().s());
    }

    @Test
    void deveConsultarPorTelefoneEUsuarioComSucesso() {
        String telefone = dummyEntity.getTelefone();
        String idUsuario = dummyEntity.getIdUsuario().toString();

        // Encadeamento de mocks para simular a resposta do DynamoDB
        when(outroContatoTable.index("UsuarioTelefoneIndex")).thenReturn(usuarioTelefoneIndex);
        when(usuarioTelefoneIndex.query(any(QueryEnhancedRequest.class))).thenReturn(pageIterable);
        when(pageIterable.stream()).thenReturn(Stream.of(page));
        when(page.items()).thenReturn(List.of(dummyEntity));

        Optional<OutroContatoEntity> resultado = repository.consultarPorTelefoneEUsuario(telefone, idUsuario);

        assertTrue(resultado.isPresent());
        assertSame(dummyEntity, resultado.get());

        verify(outroContatoTable).index("UsuarioTelefoneIndex");
        verify(usuarioTelefoneIndex).query(any(QueryEnhancedRequest.class));
    }

    @Test
    void deveConsultarPorTelefoneEUsuarioRetornarVazio() {
        String telefone = "00000000000";
        String idUsuario = UUID.randomUUID().toString();

        when(outroContatoTable.index("UsuarioTelefoneIndex")).thenReturn(usuarioTelefoneIndex);
        when(usuarioTelefoneIndex.query(any(QueryEnhancedRequest.class))).thenReturn(pageIterable);

        // Simulando que o DynamoDB não encontrou nada (retorna uma lista de itens vazia)
        when(pageIterable.stream()).thenReturn(Stream.of(page));
        when(page.items()).thenReturn(Collections.emptyList());

        Optional<OutroContatoEntity> resultado = repository.consultarPorTelefoneEUsuario(telefone, idUsuario);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveLancarExceptionQuandoDynamoFalharNoSalvar() {
        doThrow(new RuntimeException("DynamoDB fora do ar!"))
                .when(outroContatoTable).putItem(any(OutroContatoEntity.class));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> repository.salvar(dummyEntity));

        assertEquals("DynamoDB fora do ar!", exception.getMessage());
    }
}