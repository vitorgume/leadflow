package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.gateways.DashboardDataGateway;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.StatusConversa;
import com.guminteligencia.ura_chatbot_ia.domain.dashboard.ChartDataResponse;
import com.guminteligencia.ura_chatbot_ia.domain.dashboard.ContactDashboard;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardUseCaseTest {

    @Mock
    private DashboardDataGateway gateway;

    @InjectMocks
    private DashboardUseCase useCase;

    private UUID idUsuario;
    private Integer year, month, day;
    private String ddd;
    private StatusConversa status;

    @BeforeEach
    void setup() {
        idUsuario = UUID.randomUUID();
        year = 2026;
        month = 2;
        day = 20;
        ddd = "44";
        status = StatusConversa.ATIVO;
    }

    @Test
    @DisplayName("Deve retornar o total de contatos delegando ao gateway")
    void deveRetornarTotalDeContatos() {
        when(gateway.count(year, month, day, ddd, status, idUsuario)).thenReturn(150L);

        Long total = useCase.getTotalContacts(year, month, day, ddd, status, idUsuario);

        assertEquals(150L, total);
        verify(gateway).count(year, month, day, ddd, status, idUsuario);
    }

    @Test
    @DisplayName("Deve retornar os contatos de hoje delegando ao gateway")
    void deveRetornarContatosDeHoje() {
        when(gateway.count(any(LocalDate.class), eq(idUsuario))).thenReturn(15L);

        Long totalHoje = useCase.getContactsToday(idUsuario);

        assertEquals(15L, totalHoje);
        verify(gateway).count(any(LocalDate.class), eq(idUsuario));
    }

    @Test
    @DisplayName("Deve calcular a taxa de resposta corretamente com conversas mistas")
    void deveCalcularTaxaDeRespostaComSucesso() {
        ConversaAgente c1 = mock(ConversaAgente.class);
        ConversaAgente c2 = mock(ConversaAgente.class);
        ConversaAgente c3 = mock(ConversaAgente.class);
        ConversaAgente c4 = mock(ConversaAgente.class);

        // 3 ativas/andamento e 1 inativa = Total 4. Taxa = 3/4 = 0.75
        when(c1.getStatus()).thenReturn(StatusConversa.ATIVO);
        when(c2.getStatus()).thenReturn(StatusConversa.ANDAMENTO);
        when(c3.getStatus()).thenReturn(StatusConversa.ATIVO);
        when(c4.getStatus()).thenReturn(StatusConversa.INATIVO_G1);

        when(gateway.getResponseRate(year, month, day, ddd, status, idUsuario))
                .thenReturn(List.of(c1, c2, c3, c4));

        Double taxa = useCase.getResponseRate(year, month, day, ddd, status, idUsuario);

        assertEquals(0.75, taxa);
    }

    @Test
    @DisplayName("Deve retornar taxa de resposta zero se não houver conversas válidas")
    void deveRetornarTaxaDeRespostaZeroQuandoVazio() {
        when(gateway.getResponseRate(year, month, day, ddd, status, idUsuario))
                .thenReturn(Collections.emptyList());

        Double taxa = useCase.getResponseRate(year, month, day, ddd, status, idUsuario);

        assertEquals(0.0, taxa);
    }

    @Test
    @DisplayName("Deve calcular a média de contatos por vendedor corretamente")
    void deveCalcularMediaContatosPorVendedor() {
        ConversaAgente c1 = mock(ConversaAgente.class);
        ConversaAgente c2 = mock(ConversaAgente.class);
        ConversaAgente c3 = mock(ConversaAgente.class);

        Vendedor v1 = mock(Vendedor.class);
        Vendedor v2 = mock(Vendedor.class);

        // 3 conversas divididas entre 2 vendedores = Média de 1.5
        when(c1.getVendedor()).thenReturn(v1);
        when(c2.getVendedor()).thenReturn(v1);
        when(c3.getVendedor()).thenReturn(v2);

        when(gateway.getAverageContactsPerSeller(year, month, day, ddd, status, idUsuario))
                .thenReturn(List.of(c1, c2, c3));

        double media = useCase.getAverageContactsPerSeller(year, month, day, ddd, status, idUsuario);

        assertEquals(1.5, media);
    }

    @Test
    @DisplayName("Deve retornar média zero se a lista de conversas for vazia")
    void deveRetornarMediaZeroQuandoListaVazia() {
        when(gateway.getAverageContactsPerSeller(year, month, day, ddd, status, idUsuario))
                .thenReturn(Collections.emptyList());

        double media = useCase.getAverageContactsPerSeller(year, month, day, ddd, status, idUsuario);

        assertEquals(0.0, media);
    }

    @Test
    @DisplayName("Deve agrupar corretamente os contatos por dia do mês")
    void deveAgruparContatosPorDia() {
        ConversaAgente c1 = mock(ConversaAgente.class);
        ConversaAgente c2 = mock(ConversaAgente.class);
        ConversaAgente c3 = mock(ConversaAgente.class);

        // Duas conversas no dia 10, uma conversa no dia 15
        when(c1.getDataCriacao()).thenReturn(LocalDateTime.of(2026, 2, 10, 14, 0));
        when(c2.getDataCriacao()).thenReturn(LocalDateTime.of(2026, 2, 10, 16, 0));
        when(c3.getDataCriacao()).thenReturn(LocalDateTime.of(2026, 2, 15, 9, 0));

        when(gateway.getContactsByDay(year, month, ddd, status, idUsuario))
                .thenReturn(List.of(c1, c2, c3));

        ChartDataResponse response = useCase.getContactsByDay(year, month, ddd, status, idUsuario);

        assertNotNull(response);
        assertNotNull(response.getItems());
        assertEquals(2, response.getItems().size()); // Duas barras no gráfico (dia 10 e dia 15)

        // Verifica se a contagem do dia 10 é igual a 2
        boolean temDia10 = response.getItems().stream()
                .anyMatch(item -> item.getLabel().equals("10") && item.getValue().equals(2L));
        assertTrue(temDia10);
    }

    @Test
    @DisplayName("Deve agrupar corretamente os contatos pelas horas do dia")
    void deveAgruparContatosPorHora() {
        ConversaAgente c1 = mock(ConversaAgente.class);
        ConversaAgente c2 = mock(ConversaAgente.class);

        // Duas conversas no mesmo horário (08h)
        when(c1.getDataCriacao()).thenReturn(LocalDateTime.of(2026, 2, 20, 8, 15));
        when(c2.getDataCriacao()).thenReturn(LocalDateTime.of(2026, 2, 20, 8, 45));

        when(gateway.getContactsByHour(year, month, day, ddd, status, idUsuario))
                .thenReturn(List.of(c1, c2));

        ChartDataResponse response = useCase.getContactsByHour(year, month, day, ddd, status, idUsuario);

        assertNotNull(response);
        assertEquals(1, response.getItems().size()); // Apenas uma barra de hora (8)
        assertEquals("8", response.getItems().get(0).getLabel());
        assertEquals(2L, response.getItems().get(0).getValue());
    }

    @Test
    @DisplayName("Deve retornar página de contatos delegando ao gateway")
    void deveRetornarContatosPaginados() {
        Pageable pageable = PageRequest.of(0, 10);
        ContactDashboard mockContact = mock(ContactDashboard.class);
        Page<ContactDashboard> pageMock = new PageImpl<>(List.of(mockContact));

        when(gateway.getPaginatedContacts(year, month, day, ddd, status, pageable, idUsuario))
                .thenReturn(pageMock);

        Page<ContactDashboard> result = useCase.getPaginatedContacts(year, month, day, ddd, status, pageable, idUsuario);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(gateway).getPaginatedContacts(year, month, day, ddd, status, pageable, idUsuario);
    }
}