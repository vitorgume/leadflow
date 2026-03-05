package com.guminteligencia.ura_chatbot_ia.entrypoint.controller.outrosSetores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.application.usecase.MembroUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.outrosSetores.Membro;
import com.guminteligencia.ura_chatbot_ia.entrypoint.controller.MembroController;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.MembroDto;
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
public class MembroControllerUnitTest {

    @Mock
    private MembroUseCase membroUseCase;

    @InjectMocks
    private MembroController controller;

    private MockMvc mockMvc;
    private final ObjectMapper om = new ObjectMapper();

    private final UUID ID_MEMBRO = UUID.randomUUID();
    private final UUID ID_USUARIO = UUID.randomUUID();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Cadastrar: Deve retornar 201 Created")
    void cadastrarDeveRetornarCreated() throws Exception {
        // Arrange
        MembroDto dtoInput = MembroDto.builder()
                .nome("João")
                .telefone("5511999999999")
                .usuario(UsuarioDto.builder().id(ID_USUARIO).build())
                .build();

        Membro domainRetorno = Membro.builder()
                .id(ID_MEMBRO)
                .nome("João")
                .telefone("5511999999999")
                .usuario(Usuario.builder().id(ID_USUARIO).build())
                .build();

        when(membroUseCase.cadastrar(any(Membro.class))).thenReturn(domainRetorno);

        // Act & Assert
        mockMvc.perform(post("/membros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dtoInput)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/membros/" + ID_MEMBRO))
                .andExpect(jsonPath("$.dado.id").value(ID_MEMBRO.toString()))
                .andExpect(jsonPath("$.dado.nome").value("João"));
    }

    @Test
    @DisplayName("Listar: Deve retornar 200 OK com lista de membros")
    void listarDeveRetornarOk() throws Exception {
        // Arrange
        Membro domainRetorno = Membro.builder()
                .id(ID_MEMBRO)
                .nome("João")
                .telefone("5511999999999")
                .usuario(Usuario.builder().id(UUID.randomUUID()).build())
                .build();

        when(membroUseCase.listar(ID_USUARIO)).thenReturn(List.of(domainRetorno));

        // Act & Assert
        mockMvc.perform(get("/membros/{idUsuario}", ID_USUARIO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado[0].id").value(ID_MEMBRO.toString()))
                .andExpect(jsonPath("$.dado[0].nome").value("João"));
    }

    @Test
    @DisplayName("Alterar: Deve retornar 200 OK")
    void alterarDeveRetornarOk() throws Exception {
        // Arrange
        MembroDto dtoInput = MembroDto.builder()
                .nome("Nome Editado")
                .telefone("5511888888888")
                .usuario(UsuarioDto.builder().id(UUID.randomUUID()).build())
                .build();

        Membro domainRetorno = Membro.builder()
                .id(ID_MEMBRO)
                .nome("Nome Editado")
                .telefone("5511888888888")
                .usuario(Usuario.builder().id(UUID.randomUUID()).build())
                .build();

        when(membroUseCase.alterar(any(Membro.class), eq(ID_MEMBRO))).thenReturn(domainRetorno);

        // Act & Assert
        mockMvc.perform(put("/membros/{id}", ID_MEMBRO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dtoInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(ID_MEMBRO.toString()))
                .andExpect(jsonPath("$.dado.nome").value("Nome Editado"));
    }

    @Test
    @DisplayName("Deletar: Deve retornar 204 No Content")
    void deletarDeveRetornarNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/membros/{id}", ID_MEMBRO))
                .andExpect(status().isNoContent());

        verify(membroUseCase).deletar(ID_MEMBRO);
    }
}
