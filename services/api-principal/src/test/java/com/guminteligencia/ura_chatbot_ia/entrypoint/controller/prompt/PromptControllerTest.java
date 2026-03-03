package com.guminteligencia.ura_chatbot_ia.entrypoint.controller.prompt;

import com.guminteligencia.ura_chatbot_ia.application.usecase.PromptUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Prompt;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false"
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Tag("it")
class PromptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PromptUseCase promptUseCase;

    private final UUID ID_PROMPT = UUID.randomUUID();
    private final UUID ID_USUARIO = UUID.randomUUID();

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("IT - Cadastrar: Quando sucesso retorna Created")
    void cadastrarQuandoSucessoRetornaCreated() throws Exception {
        // Arrange
        Prompt domainRetorno = Prompt.builder()
                .id(ID_PROMPT)
                .titulo("Prompt de Teste")
                .prompt("Conteudo do prompt")
                .usuario(Usuario.builder().id(ID_USUARIO).build())
                .build();

        given(promptUseCase.cadastrar(any(Prompt.class))).willReturn(domainRetorno);

        String json = """
            {
              "titulo": "Prompt de Teste",
              "prompt": "Conteudo do prompt",
              "usuario": {
                "id": "%s"
              }
            }
        """.formatted(ID_USUARIO);

        // Act & Assert
        mockMvc.perform(post("/prompts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/promtps/" + ID_PROMPT))
                .andExpect(jsonPath("$.dado.id").value(ID_PROMPT.toString()))
                .andExpect(jsonPath("$.dado.titulo").value("Prompt de Teste"));
    }

    @Test
    @DisplayName("IT - Listar: Quando sucesso retorna OK")
    void listarQuandoSucessoRetornaOk() throws Exception {
        // Arrange
        Prompt domainRetorno = Prompt.builder()
                .id(ID_PROMPT)
                .titulo("Prompt de Teste")
                .usuario(Usuario.builder().id(ID_USUARIO).build())
                .build();

        given(promptUseCase.listar(ID_USUARIO)).willReturn(List.of(domainRetorno));

        // Act & Assert
        mockMvc.perform(get("/prompts/" + ID_USUARIO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado[0].id").value(ID_PROMPT.toString()))
                .andExpect(jsonPath("$.dado[0].titulo").value("Prompt de Teste"));
    }

    @Test
    @DisplayName("IT - Alterar: Quando sucesso retorna OK")
    void alterarQuandoSucessoRetornaOk() throws Exception {
        // Arrange
        Prompt domainRetorno = Prompt.builder()
                .id(ID_PROMPT)
                .titulo("Título Alterado")
                .usuario(Usuario.builder().id(ID_USUARIO).build())
                .build();

        given(promptUseCase.alterar(eq(ID_PROMPT), any(Prompt.class))).willReturn(domainRetorno);

        String json = """
            {
              "titulo": "Título Alterado",
              "usuario": {
                "id": "%s"
              }
            }
        """.formatted(ID_USUARIO);

        // Act & Assert
        mockMvc.perform(put("/prompts/" + ID_PROMPT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(ID_PROMPT.toString()))
                .andExpect(jsonPath("$.dado.titulo").value("Título Alterado"));
    }

    @Test
    @DisplayName("IT - Deletar: Quando sucesso retorna No Content")
    void deletarQuandoSucessoRetornaNoContent() throws Exception {
        // Act
        mockMvc.perform(delete("/prompts/" + ID_PROMPT))
                .andExpect(status().isNoContent());

        // Assert
        verify(promptUseCase).deletar(ID_PROMPT);
    }
}