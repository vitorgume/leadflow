package com.guminteligencia.ura_chatbot_ia.entrypoint.controller.condicao;

import com.guminteligencia.ura_chatbot_ia.domain.vendedor.ConectorLogico;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.OperadorLogico;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.CondicaoRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.CondicaoEntity;
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

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false"
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Tag("it")
class CondicaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CondicaoRepository repository;

    private final UUID ID_CONDICAO = UUID.randomUUID();

    @Test
    @DisplayName("Alterar: Quando sucesso retorna OK e JSON atualizado")
    void alterarQuandoSucessoRetornaOk() throws Exception {
        // Arrange
        // Simula a entidade existente no banco
        CondicaoEntity existing = CondicaoEntity.builder()
                .id(ID_CONDICAO)
                .campo("setor")
                .valor("Varejo")
                .operadorLogico(OperadorLogico.EQUAL)
                .conectorLogico(ConectorLogico.OR)
                .build();

        given(repository.findById(ID_CONDICAO)).willReturn(Optional.of(existing));

        // Simula o retorno do save
        CondicaoEntity updated = CondicaoEntity.builder()
                .id(ID_CONDICAO)
                .campo("faturamento")
                .valor("5000")
                .operadorLogico(OperadorLogico.IS_GREATER_THAN)
                .conectorLogico(ConectorLogico.AND)
                .build();

        given(repository.save(any(CondicaoEntity.class))).willReturn(updated);

        String json = """
            {
              "campo": "faturamento",
              "valor": "5000",
              "operador_logico": "IS_GREATER_THAN",
              "conector_logico": "AND"
            }
        """;

        // Act & Assert
        mockMvc.perform(put("/condicoes/" + ID_CONDICAO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(ID_CONDICAO.toString()))
                .andExpect(jsonPath("$.dado.campo").value("faturamento"))
                .andExpect(jsonPath("$.dado.valor").value("5000"))
                .andExpect(jsonPath("$.dado.operador_logico").value("IS_GREATER_THAN"));
    }

    @Test
    @DisplayName("Deletar: Quando sucesso retorna No Content")
    void deletarQuandoSucessoRetornaNoContent() throws Exception {
        // Arrange
        // Geralmente o DataProvider faz um findById antes de deletar (verifique sua implementação)
        // Se fizer, precisamos mockar o findById também.
        CondicaoEntity existing = CondicaoEntity.builder().id(ID_CONDICAO).build();
        given(repository.findById(ID_CONDICAO)).willReturn(Optional.of(existing));

        // Act & Assert
        mockMvc.perform(delete("/condicoes/" + ID_CONDICAO))
                .andExpect(status().isNoContent());

        // Verify
        Mockito.verify(repository).deleteById(ID_CONDICAO);
    }

    @Test
    @DisplayName("Deletar: Quando erro interno retorna 500")
    void deletarQuandoErroRetornaInternalServerError() throws Exception {
        // Arrange
        given(repository.findById(ID_CONDICAO)).willReturn(Optional.of(CondicaoEntity.builder().build()));

        willThrow(new RuntimeException("Erro ao deletar banco"))
                .given(repository).deleteById(ID_CONDICAO);

        // Act & Assert
        mockMvc.perform(delete("/condicoes/" + ID_CONDICAO))
                .andExpect(status().isInternalServerError());
    }
}