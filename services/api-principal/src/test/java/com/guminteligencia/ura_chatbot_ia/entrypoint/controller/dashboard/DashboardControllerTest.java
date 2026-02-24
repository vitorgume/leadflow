package com.guminteligencia.ura_chatbot_ia.entrypoint.controller.dashboard;

import com.guminteligencia.ura_chatbot_ia.application.usecase.DashboardUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.StatusConversa;
import com.guminteligencia.ura_chatbot_ia.domain.dashboard.ChartDataResponse;
import com.guminteligencia.ura_chatbot_ia.domain.dashboard.ContactDashboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false"
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Tag("it")
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardUseCase dashboardUseCase;

    private final UUID ID_USUARIO = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        // Setup inicial se necessário
    }

    @Test
    @DisplayName("IT - Deve retornar o total de contatos com sucesso")
    void deveRetornarTotalContatosIntegrado() throws Exception {
        given(dashboardUseCase.getTotalContacts(2026, 2, 20, "44", StatusConversa.ATIVO, ID_USUARIO))
                .willReturn(150L);

        mockMvc.perform(get("/dashboard/total-contatos")
                        .param("year", "2026")
                        .param("month", "2")
                        .param("day", "20")
                        .param("ddd", "44")
                        .param("status", "ATIVO")
                        .param("idUsuario", ID_USUARIO.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(150));
    }

    @Test
    @DisplayName("IT - Deve retornar o total de contatos de hoje")
    void deveRetornarContatosHojeIntegrado() throws Exception {
        given(dashboardUseCase.getContactsToday(ID_USUARIO))
                .willReturn(25L);

        mockMvc.perform(get("/dashboard/contatos-hoje/{idUsuario}", ID_USUARIO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(25));
    }

    @Test
    @DisplayName("IT - Deve retornar taxa de resposta populando o ModelAttribute")
    void deveRetornarTaxaRespostaIntegrado() throws Exception {
        given(dashboardUseCase.getResponseRate(2026, null, null, "44", StatusConversa.ATIVO, ID_USUARIO))
                .willReturn(0.85);

        mockMvc.perform(get("/dashboard/taxa-resposta")
                        .param("year", "2026")
                        .param("ddd", "44")
                        .param("status", "ATIVO")
                        .param("idUsuario", ID_USUARIO.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(0.85));
    }

    @Test
    @DisplayName("IT - Deve retornar a média por vendedor")
    void deveRetornarMediaVendedorIntegrado() throws Exception {
        given(dashboardUseCase.getAverageContactsPerSeller(2026, null, null, null, null, ID_USUARIO))
                .willReturn(15.5);

        mockMvc.perform(get("/dashboard/media-por-vendedor")
                        .param("year", "2026")
                        .param("idUsuario", ID_USUARIO.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(15.5));
    }

    @Test
    @DisplayName("IT - Deve retornar dados do gráfico de contatos por dia")
    void deveRetornarContatosPorDiaIntegrado() throws Exception {
        ChartDataResponse response = new ChartDataResponse(
                List.of(new ChartDataResponse.ChartItem("15", 42L))
        );

        given(dashboardUseCase.getContactsByDay(2026, 2, "44", StatusConversa.ATIVO, ID_USUARIO))
                .willReturn(response);

        mockMvc.perform(get("/dashboard/contatos-por-dia")
                        .param("year", "2026")
                        .param("month", "2")
                        .param("ddd", "44")
                        .param("status", "ATIVO")
                        .param("idUsuario", ID_USUARIO.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].label").value("15"))
                .andExpect(jsonPath("$.items[0].value").value(42));
    }

    @Test
    @DisplayName("IT - Deve retornar dados do gráfico de contatos por hora")
    void deveRetornarContatosPorHoraIntegrado() throws Exception {
        ChartDataResponse response = new ChartDataResponse(
                List.of(new ChartDataResponse.ChartItem("14", 10L))
        );

        given(dashboardUseCase.getContactsByHour(2026, 2, 20, "44", StatusConversa.ATIVO, ID_USUARIO))
                .willReturn(response);

        mockMvc.perform(get("/dashboard/contatos-por-hora")
                        .param("year", "2026")
                        .param("month", "2")
                        .param("day", "20")
                        .param("ddd", "44")
                        .param("status", "ATIVO")
                        .param("idUsuario", ID_USUARIO.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].label").value("14"))
                .andExpect(jsonPath("$.items[0].value").value(10));
    }

    @Test
    @DisplayName("IT - Deve retornar a lista paginada de contatos")
    void deveRetornarContatosPaginadoIntegrado() throws Exception {
        ContactDashboard contact = new ContactDashboard("João Silva", "4499999999", StatusConversa.ATIVO);
        Page<ContactDashboard> page = new PageImpl<>(List.of(contact), PageRequest.of(0, 10), 1);

        given(dashboardUseCase.getPaginatedContacts(
                eq(2026), eq(2), eq(20), eq("44"), eq(StatusConversa.ATIVO), any(Pageable.class), eq(ID_USUARIO)
        )).willReturn(page);

        mockMvc.perform(get("/dashboard/contatos-paginado")
                        .param("year", "2026")
                        .param("month", "2")
                        .param("day", "20")
                        .param("ddd", "44")
                        .param("status", "ATIVO")
                        .param("idUsuario", ID_USUARIO.toString())
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contacts[0].nome").value("João Silva"))
                .andExpect(jsonPath("$.contacts[0].telefone").value("4499999999"))
                .andExpect(jsonPath("$.contacts[0].status").value("ATIVO"))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
