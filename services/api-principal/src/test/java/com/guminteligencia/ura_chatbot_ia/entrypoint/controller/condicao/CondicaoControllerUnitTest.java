package com.guminteligencia.ura_chatbot_ia.entrypoint.controller.condicao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.CondicaoUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Condicao;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.ConectorLogico;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.OperadorLogico;
import com.guminteligencia.ura_chatbot_ia.entrypoint.controller.CondicaoController;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.vendedor.CondicaoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CondicaoControllerUnitTest {

    @Mock
    private CondicaoUseCase condicaoUseCase;

    @InjectMocks
    private CondicaoController controller;

    private MockMvc mockMvc;
    private final ObjectMapper om = new ObjectMapper();
    private final UUID ID = UUID.randomUUID();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Alterar: Deve retornar 200 OK com os dados atualizados")
    void alterarDeveRetornarOk() throws Exception {
        // Arrange
        CondicaoDto dtoInput = CondicaoDto.builder()
                .campo("faturamento")
                .valor("1000")
                .operadorLogico(OperadorLogico.IS_GREATER_THAN)
                .conectorLogico(ConectorLogico.AND)
                .build();

        Condicao condicaoRetorno = Condicao.builder()
                .id(ID)
                .campo("faturamento")
                .valor("1000")
                .operadorLogico(OperadorLogico.IS_GREATER_THAN)
                .conectorLogico(ConectorLogico.AND)
                .build();

        // Mock do UseCase
        // Nota: O Controller chama o Mapper estático antes de chamar o UseCase.
        // Assumimos que o Mapper funciona (pois já foi testado) e o mock recebe o objeto de domínio.
        when(condicaoUseCase.alterar(eq(ID), any(Condicao.class))).thenReturn(condicaoRetorno);

        // Act & Assert
        mockMvc.perform(put("/condicoes/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dtoInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(ID.toString()))
                .andExpect(jsonPath("$.dado.campo").value("faturamento"))
                .andExpect(jsonPath("$.dado.valor").value("1000"));
    }

    @Test
    @DisplayName("Deletar: Deve retornar 204 No Content")
    void deletarDeveRetornarNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/condicoes/{id}", ID))
                .andExpect(status().isNoContent());

        verify(condicaoUseCase).deletar(ID);
    }
}
