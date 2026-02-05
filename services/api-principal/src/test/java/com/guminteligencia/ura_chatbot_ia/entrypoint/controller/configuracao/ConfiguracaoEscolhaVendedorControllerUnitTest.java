package com.guminteligencia.ura_chatbot_ia.entrypoint.controller.configuracao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.ConfiguracaoEscolhaVendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.ConfiguracaoEscolhaVendedor;
import com.guminteligencia.ura_chatbot_ia.entrypoint.controller.ConfiguracaoEscolhaVendedorController;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.UsuarioDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.vendedor.ConfiguracaoEscolhaVendedorDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ConfiguracaoEscolhaVendedorControllerUnitTest {

    @Mock
    private ConfiguracaoEscolhaVendedorUseCase useCase;

    @InjectMocks
    private ConfiguracaoEscolhaVendedorController controller;

    private MockMvc mockMvc;
    private final ObjectMapper om = new ObjectMapper();
    private final UUID ID_CONFIG = UUID.randomUUID();
    private final UUID ID_USUARIO = UUID.randomUUID();

    private Usuario usuario;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        usuario = Usuario.builder()
                .id(ID_USUARIO)
                .nome("nome teste")
                .telefone("00000000000")
                .senha("senhateste123")
                .email("emailteste@123")
                .telefoneConectado("00000000000")
                .atributosQualificacao(Map.of("teste", "valor_teste"))
                .configuracaoCrm(
                        ConfiguracaoCrm.builder()
                                .crmType(CrmType.KOMMO)
                                .mapeamentoCampos(Map.of("teste", "teste"))
                                .idTagAtivo("id-teste")
                                .idTagAtivo("id-teste")
                                .idEtapaAtivos("id-teste")
                                .idEtapaInativos("id-teste")
                                .acessToken("acess-token-teste")
                                .build()
                )
                .mensagemDirecionamentoVendedor("mensagem-teste")
                .mensagemRecontatoG1("mensagem-teste")
                .whatsappToken("token-teste")
                .whatsappIdInstance("id-teste")
                .agenteApiKey("api-key-teste")
                .build();
    }

    @Test
    @DisplayName("Cadastrar: Deve retornar 201 Created com Location")
    void cadastrarDeveRetornarCreated() throws Exception {
        // Arrange
        ConfiguracaoEscolhaVendedorDto dtoInput = ConfiguracaoEscolhaVendedorDto.builder()
                .prioridade(1)
                .usuario(UsuarioDto.builder().id(ID_USUARIO).build())
                .vendedores(Collections.emptyList())
                .condicoes(Collections.emptyList())
                .build();

        ConfiguracaoEscolhaVendedor domainRetorno = ConfiguracaoEscolhaVendedor.builder()
                .id(ID_CONFIG)
                .prioridade(1)
                .usuario(usuario)
                .vendedores(Collections.emptyList())
                .condicoes(Collections.emptyList())
                .build();

        when(useCase.cadastrar(any(ConfiguracaoEscolhaVendedor.class))).thenReturn(domainRetorno);

        // Act & Assert
        mockMvc.perform(post("/configuracoes-escolha-vendedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dtoInput)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/configuracoes-escolha-vendedores/" + ID_CONFIG))
                .andExpect(jsonPath("$.dado.id").value(ID_CONFIG.toString()))
                .andExpect(jsonPath("$.dado.prioridade").value(1));
    }

    @Test
    @DisplayName("Listar: Deve retornar 200 OK com Paginação")
    void listarDeveRetornarOk() throws Exception {
        // Arrange
        ConfiguracaoEscolhaVendedor domain = ConfiguracaoEscolhaVendedor.builder()
                .id(ID_CONFIG)
                .prioridade(1)
                .usuario(usuario)
                .vendedores(Collections.emptyList())
                .condicoes(Collections.emptyList())
                .build();

        // --- CORREÇÃO AQUI ---
        // Usamos PageRequest.of(0, 10) para evitar o Unpaged que quebra a serialização
        Pageable pageable = PageRequest.of(0, 10);
        Page<ConfiguracaoEscolhaVendedor> page = new PageImpl<>(List.of(domain), pageable, 1);

        when(useCase.listarPorUsuarioPaginado(eq(ID_USUARIO), any(Pageable.class)))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/configuracoes-escolha-vendedores/listar/{idUsuario}", ID_USUARIO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.content[0].id").value(ID_CONFIG.toString()));
    }

    @Test
    @DisplayName("Alterar: Deve retornar 200 OK com dados atualizados")
    void alterarDeveRetornarOk() throws Exception {
        // Arrange
        ConfiguracaoEscolhaVendedorDto dtoInput = ConfiguracaoEscolhaVendedorDto.builder()
                .prioridade(2)
                .usuario(UsuarioDto.builder().id(ID_USUARIO).build())
                .build();

        ConfiguracaoEscolhaVendedor domainRetorno = ConfiguracaoEscolhaVendedor.builder()
                .id(ID_CONFIG)
                .prioridade(2)
                .usuario(usuario)
                .vendedores(new ArrayList<>())
                .condicoes(new ArrayList<>())
                .build();

        when(useCase.alterar(eq(ID_CONFIG), any(ConfiguracaoEscolhaVendedor.class)))
                .thenReturn(domainRetorno);

        // Act & Assert
        mockMvc.perform(put("/configuracoes-escolha-vendedores/{id}", ID_CONFIG)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dtoInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(ID_CONFIG.toString()))
                .andExpect(jsonPath("$.dado.prioridade").value(2));
    }

    @Test
    @DisplayName("Deletar: Deve retornar 204 No Content")
    void deletarDeveRetornarNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/configuracoes-escolha-vendedores/{id}", ID_CONFIG))
                .andExpect(status().isNoContent());

        verify(useCase).deletar(ID_CONFIG);
    }
}
