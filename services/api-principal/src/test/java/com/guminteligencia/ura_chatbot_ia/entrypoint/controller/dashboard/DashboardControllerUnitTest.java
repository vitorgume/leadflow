package com.guminteligencia.ura_chatbot_ia.entrypoint.controller.dashboard;

import com.guminteligencia.ura_chatbot_ia.application.usecase.DashboardUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.StatusConversa;
import com.guminteligencia.ura_chatbot_ia.domain.dashboard.ChartDataResponse;
import com.guminteligencia.ura_chatbot_ia.domain.dashboard.ContactDashboard;
import com.guminteligencia.ura_chatbot_ia.entrypoint.controller.DashboardController;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DashboardControllerUnitTest {

    @Mock
    private DashboardUseCase dashboardUseCase;

    @InjectMocks
    private DashboardController controller;

    private MockMvc mockMvc;
    private final UUID ID_USUARIO = UUID.randomUUID();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GetTotalContacts: Deve retornar 200 OK com o total")
    void getTotalContactsDeveRetornarOk() throws Exception {
        when(dashboardUseCase.getTotalContacts(2026, 2, null, null, null, ID_USUARIO))
                .thenReturn(150L);

        mockMvc.perform(get("/dashboard/total-contatos")
                        .param("year", "2026")
                        .param("month", "2")
                        .param("idUsuario", ID_USUARIO.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.value").value(150));
    }

    @Test
    @DisplayName("GetContactsToday: Deve retornar 200 OK com o total de hoje")
    void getContactsTodayDeveRetornarOk() throws Exception {
        when(dashboardUseCase.getContactsToday(ID_USUARIO)).thenReturn(25L);

        mockMvc.perform(get("/dashboard/contatos-hoje/{idUsuario}", ID_USUARIO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.value").value(25));
    }

    @Test
    @DisplayName("GetContactsByDay: Deve retornar 200 OK com os dados do gráfico")
    void getContactsByDayDeveRetornarOk() throws Exception {
        ChartDataResponse.ChartItem item = new ChartDataResponse.ChartItem("20", 50L);
        ChartDataResponse response = new ChartDataResponse(List.of(item));

        when(dashboardUseCase.getContactsByDay(2026, 2, null, null, ID_USUARIO))
                .thenReturn(response);

        mockMvc.perform(get("/dashboard/contatos-por-dia")
                        .param("year", "2026")
                        .param("month", "2")
                        .param("idUsuario", ID_USUARIO.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.items[0].label").value("20"))
                .andExpect(jsonPath("$.dado.items[0].value").value(50));
    }

    @Test
    @DisplayName("GetPaginatedContacts: Deve retornar 200 OK com a lista de contatos")
    void getPaginatedContactsDeveRetornarOk() throws Exception {
        ContactDashboard contato = new ContactDashboard("João", "4499999999", StatusConversa.ATIVO);
        Page<ContactDashboard> page = new PageImpl<>(List.of(contato), PageRequest.of(0, 10), 1);

        when(dashboardUseCase.getPaginatedContacts(any(), any(), any(), any(), any(), any(), eq(ID_USUARIO)))
                .thenReturn(page);

        mockMvc.perform(get("/dashboard/contatos-paginado")
                        .param("idUsuario", ID_USUARIO.toString())
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.contacts[0].nome").value("João"))
                .andExpect(jsonPath("$.dado.contacts[0].telefone").value("4499999999"))
                .andExpect(jsonPath("$.dado.totalPages").value(1))
                .andExpect(jsonPath("$.dado.totalElements").value(1));
    }
}