package com.guminteligencia.ura_chatbot_ia.entrypoint.controller.relatorio;

import com.guminteligencia.ura_chatbot_ia.application.usecase.RelatorioUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false"
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Tag("it")
class RelatorioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Mockamos o UseCase para evitar que o teste tente enviar e-mails reais
    // ou precise de dependências profundas que não conhecemos.
    @MockitoBean
    private RelatorioUseCase relatorioUseCase;

    @Test
    @DisplayName("Gerar: Quando chamado retorna 200 OK")
    void gerarRelatorioQuandoSucessoRetornaOk() throws Exception {
        // Act
        mockMvc.perform(post("/relatorios/gerar"))
                .andExpect(status().isOk());

        // Assert
        // Garante que o Bean do UseCase foi chamado corretamente dentro do contexto Spring
        verify(relatorioUseCase).enviarRelatorioDiarioVendedores();
    }

}