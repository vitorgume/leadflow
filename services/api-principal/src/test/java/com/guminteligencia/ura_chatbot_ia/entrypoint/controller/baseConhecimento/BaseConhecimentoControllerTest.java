package com.guminteligencia.ura_chatbot_ia.entrypoint.controller.baseConhecimento;

import com.guminteligencia.ura_chatbot_ia.application.usecase.BaseConhecimentoUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.BaseConhecimento;
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
class BaseConhecimentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BaseConhecimentoUseCase baseConhecimentoUseCase;

    private final UUID ID_BASE = UUID.randomUUID();
    private final UUID ID_USUARIO = UUID.randomUUID();

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("IT - Cadastrar: Quando sucesso retorna Created")
    void cadastrarQuandoSucessoRetornaCreated() throws Exception {
        // Arrange
        BaseConhecimento domainRetorno = BaseConhecimento.builder()
                .id(ID_BASE)
                .titulo("FAQ Integração")
                .conteudo("Conteúdo da base")
                .usuario(Usuario.builder().id(ID_USUARIO).build())
                .build();

        given(baseConhecimentoUseCase.cadastrar(any(BaseConhecimento.class))).willReturn(domainRetorno);

        String json = """
            {
              "titulo": "FAQ Integração",
              "conteudo": "Conteúdo da base",
              "usuario": {
                "id": "%s"
              }
            }
        """.formatted(ID_USUARIO);

        // Act & Assert
        mockMvc.perform(post("/base-conhecimento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                // O header está testando a string do seu Controller atual (/promtps)
                .andExpect(header().string("Location", "/promtps/" + ID_BASE))
                .andExpect(jsonPath("$.dado.id").value(ID_BASE.toString()))
                .andExpect(jsonPath("$.dado.titulo").value("FAQ Integração"));
    }

    @Test
    @DisplayName("IT - Listar: Quando sucesso retorna OK")
    void listarQuandoSucessoRetornaOk() throws Exception {
        // Arrange
        BaseConhecimento domainRetorno = BaseConhecimento.builder()
                .id(ID_BASE)
                .titulo("FAQ Integração")
                .usuario(Usuario.builder().id(ID_USUARIO).build())
                .build();

        given(baseConhecimentoUseCase.listar(ID_USUARIO)).willReturn(List.of(domainRetorno));

        // Act & Assert
        mockMvc.perform(get("/base-conhecimento/" + ID_USUARIO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado[0].id").value(ID_BASE.toString()))
                .andExpect(jsonPath("$.dado[0].titulo").value("FAQ Integração"));
    }

    @Test
    @DisplayName("IT - Alterar: Quando sucesso retorna OK")
    void alterarQuandoSucessoRetornaOk() throws Exception {
        // Arrange
        BaseConhecimento domainRetorno = BaseConhecimento.builder()
                .id(ID_BASE)
                .titulo("Base Atualizada")
                .usuario(Usuario.builder().id(ID_USUARIO).build())
                .build();

        given(baseConhecimentoUseCase.alterar(eq(ID_BASE), any(BaseConhecimento.class))).willReturn(domainRetorno);

        String json = """
            {
              "titulo": "Base Atualizada",
              "usuario": {
                "id": "%s"
              }
            }
        """.formatted(ID_USUARIO);

        // Act & Assert
        mockMvc.perform(put("/base-conhecimento/" + ID_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(ID_BASE.toString()))
                .andExpect(jsonPath("$.dado.titulo").value("Base Atualizada"));
    }

    @Test
    @DisplayName("IT - Deletar: Quando sucesso retorna No Content")
    void deletarQuandoSucessoRetornaNoContent() throws Exception {
        // Act
        mockMvc.perform(delete("/base-conhecimento/" + ID_BASE))
                .andExpect(status().isNoContent());

        // Assert
        verify(baseConhecimentoUseCase).deletar(ID_BASE);
    }
}