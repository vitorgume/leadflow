package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import static org.junit.jupiter.api.Assertions.*;

import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.StatusConversa;
import com.guminteligencia.ura_chatbot_ia.domain.dashboard.ContactDashboard;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.ConversaAgenteMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ClienteEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConversaAgenteEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardDataProviderTest {

    @Mock
    private ConversaAgenteDataProvider conversaAgenteDataProvider;

    @InjectMocks
    private DashboardDataProvider provider;

    private UUID idUsuario;
    private Pageable pageable;
    private ConversaAgenteEntity conversaEntity;
    private ConversaAgente conversaDomain;
    private ClienteEntity clienteEntity;

    @BeforeEach
    void setup() {
        idUsuario = UUID.randomUUID();
        pageable = PageRequest.of(0, 10);

        // Prepara o ClienteEntity para evitar NullPointerException no convertToContactDto
        clienteEntity = new ClienteEntity();
        clienteEntity.setNome("João Teste");
        clienteEntity.setTelefone("554499999999");

        // Prepara o ConversaAgenteEntity
        conversaEntity = new ConversaAgenteEntity();
        conversaEntity.setCliente(clienteEntity);
        conversaEntity.setStatus(StatusConversa.ATIVO);

        // Prepara o Domain mockado para os retornos
        conversaDomain = mock(ConversaAgente.class);
    }

    @Test
    @DisplayName("Deve retornar contatos paginados mapeados para ContactDashboard com sucesso")
    void deveRetornarContatosPaginadosComSucesso() {
        // Arrange
        Page<ConversaAgenteEntity> pageEntity = new PageImpl<>(List.of(conversaEntity));

        when(conversaAgenteDataProvider.findAllPage(any(Specification.class), eq(pageable)))
                .thenReturn(pageEntity);

        // Act
        Page<ContactDashboard> resultado = provider.getPaginatedContacts(
                2026, 2, 20, "44", StatusConversa.ATIVO, pageable, idUsuario);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());

        ContactDashboard dtoResult = resultado.getContent().get(0);
        assertEquals("João Teste", dtoResult.getNome());
        assertEquals("554499999999", dtoResult.getTelefone());
        assertEquals(StatusConversa.ATIVO, dtoResult.getStatus());

        verify(conversaAgenteDataProvider).findAllPage(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Deve retornar lista de conversas para cálculo de média por vendedor")
    void deveRetornarMediaContatosPorVendedor() {
        // Arrange
        List<ConversaAgenteEntity> listaEntities = List.of(conversaEntity);
        when(conversaAgenteDataProvider.findAllList(any(Specification.class)))
                .thenReturn(listaEntities);

        try (MockedStatic<ConversaAgenteMapper> ms = mockStatic(ConversaAgenteMapper.class)) {
            ms.when(() -> ConversaAgenteMapper.paraDomain(conversaEntity))
                    .thenReturn(conversaDomain);

            // Act
            List<ConversaAgente> resultado = provider.getAverageContactsPerSeller(
                    2026, 2, 20, "44", StatusConversa.ATIVO, idUsuario);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertSame(conversaDomain, resultado.get(0));

            verify(conversaAgenteDataProvider).findAllList(any(Specification.class));
            ms.verify(() -> ConversaAgenteMapper.paraDomain(conversaEntity));
        }
    }

    @Test
    @DisplayName("Deve retornar lista de conversas para taxa de resposta")
    void deveRetornarTaxaDeResposta() {
        // Arrange
        List<ConversaAgenteEntity> listaEntities = List.of(conversaEntity);
        when(conversaAgenteDataProvider.findAllList(any(Specification.class)))
                .thenReturn(listaEntities);

        try (MockedStatic<ConversaAgenteMapper> ms = mockStatic(ConversaAgenteMapper.class)) {
            ms.when(() -> ConversaAgenteMapper.paraDomain(conversaEntity))
                    .thenReturn(conversaDomain);

            // Act
            List<ConversaAgente> resultado = provider.getResponseRate(
                    2026, 2, 20, "44", StatusConversa.ATIVO, idUsuario);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertSame(conversaDomain, resultado.get(0));
        }
    }

    @Test
    @DisplayName("Deve retornar a contagem de conversas baseada nos filtros")
    void deveRetornarContagemComFiltros() {
        // Arrange
        Long expectedCount = 42L;
        when(conversaAgenteDataProvider.count(any(Specification.class)))
                .thenReturn(expectedCount);

        // Act
        Long resultado = provider.count(2026, 2, 20, "44", StatusConversa.ATIVO, idUsuario);

        // Assert
        assertEquals(expectedCount, resultado);
        verify(conversaAgenteDataProvider).count(any(Specification.class));
    }

    @Test
    @DisplayName("Deve retornar a contagem de conversas para o dia atual")
    void deveRetornarContagemDoDiaAtual() {
        // Arrange
        Long expectedCount = 15L;
        LocalDate hoje = LocalDate.of(2026, 2, 20);

        when(conversaAgenteDataProvider.count(any(Specification.class)))
                .thenReturn(expectedCount);

        // Act
        Long resultado = provider.count(hoje, idUsuario);

        // Assert
        assertEquals(expectedCount, resultado);
        verify(conversaAgenteDataProvider).count(any(Specification.class));
    }

    @Test
    @DisplayName("Deve retornar lista de conversas por hora")
    void deveRetornarContatosPorHora() {
        // Arrange
        List<ConversaAgenteEntity> listaEntities = List.of(conversaEntity);
        when(conversaAgenteDataProvider.findAllList(any(Specification.class)))
                .thenReturn(listaEntities);

        try (MockedStatic<ConversaAgenteMapper> ms = mockStatic(ConversaAgenteMapper.class)) {
            ms.when(() -> ConversaAgenteMapper.paraDomain(conversaEntity))
                    .thenReturn(conversaDomain);

            // Act
            List<ConversaAgente> resultado = provider.getContactsByHour(
                    2026, 2, 20, "44", StatusConversa.ATIVO, idUsuario);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
        }
    }

    @Test
    @DisplayName("Deve retornar lista de conversas por dia")
    void deveRetornarContatosPorDia() {
        // Arrange
        List<ConversaAgenteEntity> listaEntities = List.of(conversaEntity);
        when(conversaAgenteDataProvider.findAllList(any(Specification.class)))
                .thenReturn(listaEntities);

        try (MockedStatic<ConversaAgenteMapper> ms = mockStatic(ConversaAgenteMapper.class)) {
            ms.when(() -> ConversaAgenteMapper.paraDomain(conversaEntity))
                    .thenReturn(conversaDomain);

            // Act
            List<ConversaAgente> resultado = provider.getContactsByDay(
                    2026, 2, "44", StatusConversa.ATIVO, idUsuario);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
        }
    }
}