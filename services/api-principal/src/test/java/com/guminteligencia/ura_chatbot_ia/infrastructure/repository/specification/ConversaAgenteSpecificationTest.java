package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.specification;

import com.guminteligencia.ura_chatbot_ia.domain.StatusConversa;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConversaAgenteEntity;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversaAgenteSpecificationTest {

    @Mock
    private Root<ConversaAgenteEntity> root;
    @Mock
    private CriteriaQuery<?> query;
    @Mock
    private CriteriaBuilder cb;

    // Mocks dos caminhos (Paths) das propriedades dentro da Entidade
    @Mock private Path<Object> dataCriacaoPath;
    @Mock private Path<Object> statusPath;
    @Mock private Path<Object> clientePath;
    @Mock private Path<Object> telefonePath;
    @Mock private Path<Object> usuarioPath;
    @Mock private Path<Object> idUsuarioPath;

    @Mock private Expression<Integer> expressionFunctionMock;
    @Mock private Predicate predicateMock;

    private final UUID idUsuario = UUID.randomUUID();

    @BeforeEach
    void setup() {
        // Usamos lenient() porque alguns testes não vão usar todos os caminhos
        lenient().when(root.get("dataCriacao")).thenReturn(dataCriacaoPath);
        lenient().when(root.get("status")).thenReturn(statusPath);

        // Caminho complexo: root.get("cliente").get("telefone")
        lenient().when(root.get("cliente")).thenReturn(clientePath);
        lenient().when(clientePath.get("telefone")).thenReturn(telefonePath);

        // Caminho complexo: root.get("cliente").get("usuario").get("id")
        lenient().when(clientePath.get("usuario")).thenReturn(usuarioPath);
        lenient().when(usuarioPath.get("id")).thenReturn(idUsuarioPath);

        // Mocks padrão do CriteriaBuilder para evitar NullPointerException no cb.and(...)
        lenient().when(cb.function(anyString(), eq(Integer.class), any())).thenReturn(expressionFunctionMock);
        lenient().when(cb.equal(any(), any())).thenReturn(predicateMock);
        lenient().when(cb.like(any(Expression.class), anyString())).thenReturn(predicateMock);
        lenient().when(cb.and(any(Predicate[].class))).thenReturn(predicateMock);
    }

    @Test
    @DisplayName("Deve retornar Specification sem filtros quando todos parâmetros forem nulos")
    void deveRetornarSemFiltrosQuandoParametrosNulos() {
        Specification<ConversaAgenteEntity> spec = ConversaAgenteSpecification.filterBy(
                null, null, null, null, null, null
        );

        spec.toPredicate(root, query, cb);

        // Verifica se o CriteriaBuilder apenas chamou and() com um array vazio
        verify(cb).and(new Predicate[0]);
        verify(cb, never()).equal(any(), any());
        verify(cb, never()).like(any(Expression.class), anyString());
    }

    @Test
    @DisplayName("Deve adicionar filtro YEAR, MONTH e DAY quando datas informadas")
    void deveAdicionarFiltrosDeData() {
        Specification<ConversaAgenteEntity> spec = ConversaAgenteSpecification.filterBy(
                2026, 2, 20, null, null, null
        );

        spec.toPredicate(root, query, cb);

        // Verifica se invocou as funções SQL nativas de data
        verify(cb).function("YEAR", Integer.class, dataCriacaoPath);
        verify(cb).function("MONTH", Integer.class, dataCriacaoPath);
        verify(cb).function("DAY", Integer.class, dataCriacaoPath);

        // Como chamamos 3 funções, ele tem que fazer 3 equals comparando com o valor
        verify(cb).equal(expressionFunctionMock, 2026);
        verify(cb).equal(expressionFunctionMock, 2);
        verify(cb).equal(expressionFunctionMock, 20);
    }

    @Test
    @DisplayName("Deve adicionar filtro LIKE para DDD quando informado")
    void deveAdicionarFiltroDdd() {
        Specification<ConversaAgenteEntity> spec = ConversaAgenteSpecification.filterBy(
                null, null, null, "44", null, null
        );

        spec.toPredicate(root, query, cb);

        // O famoso "double cast" para driblar o compilador do Java
        verify(cb).like(eq((Expression<String>) (Object) telefonePath), eq("44%"));
    }

    @Test
    @DisplayName("Não deve adicionar filtro DDD quando a string for vazia")
    void naoDeveAdicionarFiltroDddQuandoVazio() {
        Specification<ConversaAgenteEntity> spec = ConversaAgenteSpecification.filterBy(
                null, null, null, "   ", null, null
        );

        spec.toPredicate(root, query, cb);

        verify(cb, never()).like(any(Expression.class), anyString());
    }

    @Test
    @DisplayName("Deve adicionar filtro de Status quando informado")
    void deveAdicionarFiltroStatus() {
        Specification<ConversaAgenteEntity> spec = ConversaAgenteSpecification.filterBy(
                null, null, null, null, StatusConversa.ATIVO, null
        );

        spec.toPredicate(root, query, cb);

        verify(cb).equal(statusPath, StatusConversa.ATIVO);
    }

    @Test
    @DisplayName("Deve adicionar filtro do idUsuario (relacionamento profundo) quando informado")
    void deveAdicionarFiltroIdUsuario() {
        Specification<ConversaAgenteEntity> spec = ConversaAgenteSpecification.filterBy(
                null, null, null, null, null, idUsuario
        );

        spec.toPredicate(root, query, cb);

        // Verifica se o path final do usuarioId recebeu o equal
        verify(cb).equal(idUsuarioPath, idUsuario);
    }

    @Test
    @DisplayName("Deve montar a query com múltiplos Predicates quando todos filtros presentes")
    void deveAdicionarTodosOsFiltros() {
        Specification<ConversaAgenteEntity> spec = ConversaAgenteSpecification.filterBy(
                2026, 2, 20, "44", StatusConversa.ATIVO, idUsuario
        );

        spec.toPredicate(root, query, cb);

        verify(cb, times(3)).function(anyString(), eq(Integer.class), any());
        verify(cb).like(any(Expression.class), eq("44%"));
        verify(cb).equal(statusPath, StatusConversa.ATIVO);
        verify(cb).equal(idUsuarioPath, idUsuario);
        verify(cb).and(any(Predicate[].class)); // Verifica a montagem final do array
    }
}