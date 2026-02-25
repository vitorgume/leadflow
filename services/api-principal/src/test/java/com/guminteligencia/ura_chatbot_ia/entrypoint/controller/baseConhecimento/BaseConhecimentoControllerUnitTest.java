package com.guminteligencia.ura_chatbot_ia.entrypoint.controller.baseConhecimento;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.application.usecase.BaseConhecimentoUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.BaseConhecimento;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.entrypoint.controller.BaseConhecimentoController;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.BaseConhecimentoDto;
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
public class BaseConhecimentoControllerUnitTest {

    @Mock
    private BaseConhecimentoUseCase baseConhecimentoUseCase;

    @InjectMocks
    private BaseConhecimentoController controller;

    private MockMvc mockMvc;
    private final ObjectMapper om = new ObjectMapper();

    private final UUID ID_BASE = UUID.randomUUID();
    private final UUID ID_USUARIO = UUID.randomUUID();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Cadastrar: Deve retornar 201 Created")
    void cadastrarDeveRetornarCreated() throws Exception {
        // Arrange
        BaseConhecimentoDto dtoInput = BaseConhecimentoDto.builder()
                .titulo("Base FAQ")
                .conteudo("Conteúdo do FAQ...")
                .usuario(UsuarioDto.builder().id(ID_USUARIO).build())
                .build();

        BaseConhecimento domainRetorno = BaseConhecimento.builder()
                .id(ID_BASE)
                .titulo("Base FAQ")
                .conteudo("Conteúdo do FAQ...")
                .usuario(Usuario.builder().id(ID_USUARIO).build())
                .build();

        when(baseConhecimentoUseCase.cadastrar(any(BaseConhecimento.class))).thenReturn(domainRetorno);

        // Act & Assert
        mockMvc.perform(post("/base-conhecimento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dtoInput)))
                .andExpect(status().isCreated())
                // ATENÇÃO: Mantendo a URI "/promtps/" que está no seu Controller atual
                .andExpect(header().string("Location", "/promtps/" + ID_BASE))
                .andExpect(jsonPath("$.dado.id").value(ID_BASE.toString()))
                .andExpect(jsonPath("$.dado.titulo").value("Base FAQ"));
    }

    @Test
    @DisplayName("Listar: Deve retornar 200 OK com a lista do Usuário")
    void listarDeveRetornarOk() throws Exception {
        // Arrange
        BaseConhecimento domainRetorno = BaseConhecimento.builder()
                .id(ID_BASE)
                .titulo("Base FAQ")
                .conteudo("Conteúdo do FAQ...")
                .usuario(Usuario.builder().id(ID_USUARIO).build())
                .build();

        when(baseConhecimentoUseCase.listar(ID_USUARIO)).thenReturn(List.of(domainRetorno));

        // Act & Assert
        mockMvc.perform(get("/base-conhecimento/{idUsuario}", ID_USUARIO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado[0].id").value(ID_BASE.toString()))
                .andExpect(jsonPath("$.dado[0].titulo").value("Base FAQ"));
    }

    @Test
    @DisplayName("Alterar: Deve retornar 200 OK")
    void alterarDeveRetornarOk() throws Exception {
        // Arrange
        BaseConhecimentoDto dtoInput = BaseConhecimentoDto.builder()
                .titulo("Base Atualizada")
                .conteudo("Conteúdo Atualizado")
                .usuario(UsuarioDto.builder().id(ID_USUARIO).build())
                .build();

        BaseConhecimento domainRetorno = BaseConhecimento.builder()
                .id(ID_BASE)
                .titulo("Base Atualizada")
                .conteudo("Conteúdo Atualizado")
                .usuario(Usuario.builder().id(ID_USUARIO).build())
                .build();

        when(baseConhecimentoUseCase.alterar(eq(ID_BASE), any(BaseConhecimento.class))).thenReturn(domainRetorno);

        // Act & Assert
        mockMvc.perform(put("/base-conhecimento/{idPrompt}", ID_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dtoInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(ID_BASE.toString()))
                .andExpect(jsonPath("$.dado.titulo").value("Base Atualizada"));
    }

    @Test
    @DisplayName("Deletar: Deve retornar 204 No Content")
    void deletarDeveRetornarNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/base-conhecimento/{idPrompt}", ID_BASE))
                .andExpect(status().isNoContent());

        verify(baseConhecimentoUseCase).deletar(ID_BASE);
    }
}
