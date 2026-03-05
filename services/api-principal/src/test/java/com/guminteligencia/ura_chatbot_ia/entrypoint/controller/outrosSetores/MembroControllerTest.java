package com.guminteligencia.ura_chatbot_ia.entrypoint.controller.outrosSetores;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.MembroRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.UsuarioRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.MembroEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.UsuarioEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"spring.task.scheduling.enabled=false"})
@AutoConfigureMockMvc(addFilters = false)
@Tag("it")
class MembroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MembroRepository membroRepository;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    private final UUID ID_MEMBRO = UUID.randomUUID();
    private final UUID ID_USUARIO = UUID.fromString("be8c66e8-b8b2-422b-be44-32307c0eb625");

    private MembroEntity membroEntity;
    private UsuarioEntity usuarioEntity;

    @BeforeEach
    void setUp() {
        usuarioEntity = UsuarioEntity.builder()
                .id(ID_USUARIO)
                .nome("Admin")
                .build();

        membroEntity = MembroEntity.builder()
                .id(ID_MEMBRO)
                .nome("João")
                .telefone("5511999999999")
                .usuario(usuarioEntity)
                .build();
    }

    @Test
    @DisplayName("Cadastrar: Quando sucesso retorna Created")
    void cadastrarQuandoSucessoRetornaCreated() throws Exception {
        // Arrange
        // 1. O UseCase consulta se o telefone já existe
        given(membroRepository.findByTelefone("5511999999999")).willReturn(Optional.empty());

        // 2. O UseCase valida se o usuário informado no payload existe
        given(usuarioRepository.findById(ID_USUARIO)).willReturn(Optional.of(usuarioEntity));

        // 3. Salva a entidade
        given(membroRepository.save(any(MembroEntity.class))).willReturn(membroEntity);

        String json = """
            {
              "nome": "João",
              "telefone": "5511999999999",
              "usuario": {
                "id": "%s"
              }
            }
        """.formatted(ID_USUARIO);

        // Act & Assert
        mockMvc.perform(post("/membros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/membros/" + ID_MEMBRO))
                .andExpect(jsonPath("$.dado.id").value(ID_MEMBRO.toString()))
                .andExpect(jsonPath("$.dado.nome").value("João"));
    }

    @Test
    @DisplayName("Listar: Quando sucesso retorna OK")
    void listarQuandoSucessoRetornaOk() throws Exception {
        // Arrange
        given(membroRepository.findByUsuario_Id(ID_USUARIO)).willReturn(List.of(membroEntity));

        // Act & Assert
        mockMvc.perform(get("/membros/" + ID_USUARIO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado[0].id").value(ID_MEMBRO.toString()))
                .andExpect(jsonPath("$.dado[0].nome").value("João"));
    }

    @Test
    @DisplayName("Alterar: Quando sucesso retorna OK")
    void alterarQuandoSucessoRetornaOk() throws Exception {
        // Arrange
        MembroEntity entityEditada = MembroEntity.builder()
                .id(ID_MEMBRO)
                .nome("João Editado")
                .telefone("5511888888888")
                .usuario(usuarioEntity)
                .build();

        // 1. O UseCase busca o membro original
        given(membroRepository.findById(ID_MEMBRO)).willReturn(Optional.of(membroEntity));

        // 2. Como o telefone mudou, ele verifica se o novo telefone está livre
        given(membroRepository.findByTelefone("5511888888888")).willReturn(Optional.empty());

        // 3. Salva a edição
        given(membroRepository.save(any(MembroEntity.class))).willReturn(entityEditada);

        String json = """
            {
              "nome": "João Editado",
              "telefone": "5511888888888",
              "usuario": {
                "id": "be8c66e8-b8b2-422b-be44-32307c0eb625"
              }
            }
        """;

        // Act & Assert
        mockMvc.perform(put("/membros/" + ID_MEMBRO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(ID_MEMBRO.toString()))
                .andExpect(jsonPath("$.dado.nome").value("João Editado"));
    }

    @Test
    @DisplayName("Deletar: Quando sucesso retorna No Content")
    void deletarQuandoSucessoRetornaNoContent() throws Exception {
        // Arrange
        // O UseCase busca antes de deletar
        given(membroRepository.findById(ID_MEMBRO)).willReturn(Optional.of(membroEntity));

        // Act & Assert
        mockMvc.perform(delete("/membros/" + ID_MEMBRO))
                .andExpect(status().isNoContent());

        Mockito.verify(membroRepository).deleteById(ID_MEMBRO);
    }
}