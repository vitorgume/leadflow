package com.guminteligencia.ura_chatbot_ia.entrypoint.controller.prompt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.application.usecase.PromptUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Prompt;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.entrypoint.controller.PromptController;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.PromptDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.UsuarioDto;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PromptControllerUnitTest {

    @Mock
    private PromptUseCase promptUseCase;

    @InjectMocks
    private PromptController controller;

    private MockMvc mockMvc;
    private final ObjectMapper om = new ObjectMapper();

    private final UUID ID_PROMPT = UUID.randomUUID();
    private final UUID ID_USUARIO = UUID.randomUUID();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Cadastrar: Deve retornar 201 Created")
    void cadastrarDeveRetornarCreated() throws Exception {
        // Arrange
        PromptDto dtoInput = PromptDto.builder()
                .titulo("Prompt Vendas")
                .prompt("Você é um especialista em vendas...")
                .usuario(UsuarioDto.builder().id(ID_USUARIO).build())
                .build();

        Prompt domainRetorno = Prompt.builder()
                .id(ID_PROMPT)
                .titulo("Prompt Vendas")
                .prompt("Você é um especialista em vendas...")
                .usuario(Usuario.builder().id(ID_USUARIO).build())
                .build();

        when(promptUseCase.cadastrar(any(Prompt.class))).thenReturn(domainRetorno);

        // Act & Assert
        // Act & Assert
        mockMvc.perform(post("/prompts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dtoInput)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/promtps/" + ID_PROMPT)) // 👇 Corrigido aqui!
                .andExpect(jsonPath("$.dado.id").value(ID_PROMPT.toString()))
                .andExpect(jsonPath("$.dado.titulo").value("Prompt Vendas"));
    }

    @Test
    @DisplayName("Listar: Deve retornar 200 OK com a lista de Prompts do Usuário")
    void listarDeveRetornarOk() throws Exception {
        // Arrange
        Prompt domainRetorno = Prompt.builder()
                .id(ID_PROMPT)
                .titulo("Prompt Vendas")
                .prompt("Você é um especialista...")
                .usuario(Usuario.builder().id(ID_USUARIO).build())
                .build();

        when(promptUseCase.listar(ID_USUARIO)).thenReturn(List.of(domainRetorno));

        // Act & Assert
        mockMvc.perform(get("/prompts/{idUsuario}", ID_USUARIO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado[0].id").value(ID_PROMPT.toString()))
                .andExpect(jsonPath("$.dado[0].titulo").value("Prompt Vendas"));
    }

    @Test
    @DisplayName("Alterar: Deve retornar 200 OK")
    void alterarDeveRetornarOk() throws Exception {
        // Arrange
        PromptDto dtoInput = PromptDto.builder()
                .titulo("Novo Título")
                .prompt("Nova instrução")
                .usuario(UsuarioDto.builder().id(ID_USUARIO).build())
                .build();

        Prompt domainRetorno = Prompt.builder()
                .id(ID_PROMPT)
                .titulo("Novo Título")
                .prompt("Nova instrução")
                .usuario(Usuario.builder().id(ID_USUARIO).build())
                .build();

        when(promptUseCase.alterar(eq(ID_PROMPT), any(Prompt.class))).thenReturn(domainRetorno);

        // Act & Assert
        mockMvc.perform(put("/prompts/{idPrompt}", ID_PROMPT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dtoInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(ID_PROMPT.toString()))
                .andExpect(jsonPath("$.dado.titulo").value("Novo Título"));
    }

    @Test
    @DisplayName("Deletar: Deve retornar 204 No Content")
    void deletarDeveRetornarNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/prompts/{idPrompt}", ID_PROMPT))
                .andExpect(status().isNoContent());

        verify(promptUseCase).deletar(ID_PROMPT);
    }
}
