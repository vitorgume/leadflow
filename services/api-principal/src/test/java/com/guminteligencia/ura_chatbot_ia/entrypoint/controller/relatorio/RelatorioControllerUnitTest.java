package com.guminteligencia.ura_chatbot_ia.entrypoint.controller.relatorio;

import com.guminteligencia.ura_chatbot_ia.application.usecase.RelatorioUseCase;
import com.guminteligencia.ura_chatbot_ia.entrypoint.controller.RelatorioController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RelatorioControllerUnitTest {

    @Mock
    private RelatorioUseCase relatorioUseCase;

    @InjectMocks
    private RelatorioController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GerarRelatorio: Deve retornar 200 OK e chamar o UseCase")
    void gerarRelatorioDeveRetornarOk() throws Exception {
        // Act & Assert
        // Como o @RequestMapping não definiu method, ele aceita GET, POST, etc.
        // Usei POST pois semanticamente "gerar" altera estado/envia algo.
        mockMvc.perform(post("/relatorios/gerar"))
                .andExpect(status().isOk());

        // Verify
        verify(relatorioUseCase).enviarRelatorioDiarioVendedores();
    }
}
