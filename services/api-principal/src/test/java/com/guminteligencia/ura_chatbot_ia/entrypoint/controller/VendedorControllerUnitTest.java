package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.VendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Prioridade;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.vendedor.VendedorDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VendedorControllerUnitTest {

    @Mock
    private VendedorUseCase vendedorUseCase;

    @InjectMocks
    private VendedorController controller;

    private MockMvc mockMvc;
    private final ObjectMapper om = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void cadastrarDeveRetornarCreated() throws Exception {
        Vendedor vendedor = Vendedor.builder().id(1L).nome("Pedro").telefone("9999").prioridade(new Prioridade(1, true)).build();
        when(vendedorUseCase.cadastrar(any())).thenReturn(vendedor);

        VendedorDto dto = VendedorDto.builder().nome("Pedro").telefone("9999").prioridade(new Prioridade(1, true)).build();
        mockMvc.perform(post("/vendedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/vendedores/1"))
                .andExpect(jsonPath("$.dado.id").value(1));
    }

    @Test
    void alterarDeveRetornarOk() throws Exception {
        Vendedor vendedor = Vendedor.builder().id(2L).nome("Novo").telefone("8888").prioridade(new Prioridade(1, false)).build();
        when(vendedorUseCase.alterar(any(), any())).thenReturn(vendedor);

        VendedorDto dto = VendedorDto.builder().nome("Novo").telefone("8888").prioridade(new Prioridade(1, false)).build();
        mockMvc.perform(put("/vendedores/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(2))
                .andExpect(jsonPath("$.dado.nome").value("Novo"));
    }

    @Test
    void listarDeveRetornarOk() throws Exception {
        when(vendedorUseCase.listar()).thenReturn(List.of(
                Vendedor.builder().id(1L).nome("A").telefone("1").build(),
                Vendedor.builder().id(2L).nome("B").telefone("2").build()
        ));

        mockMvc.perform(get("/vendedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado[0].id").value(1))
                .andExpect(jsonPath("$.dado[1].id").value(2));
    }

    @Test
    void deletarDeveRetornarNoContent() throws Exception {
        mockMvc.perform(delete("/vendedores/3"))
                .andExpect(status().isNoContent());
        verify(vendedorUseCase).deletar(3L);
    }
}
