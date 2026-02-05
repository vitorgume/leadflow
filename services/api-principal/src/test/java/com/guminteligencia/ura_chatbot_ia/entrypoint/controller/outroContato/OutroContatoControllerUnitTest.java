package com.guminteligencia.ura_chatbot_ia.entrypoint.controller.outroContato;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.application.usecase.OutroContatoUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.*;
import com.guminteligencia.ura_chatbot_ia.entrypoint.controller.OutroContatoController;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ConfiguracaoCrmDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.OutroContatoDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.UsuarioDto;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class OutroContatoControllerUnitTest {

    @Mock
    private OutroContatoUseCase useCase;

    @InjectMocks
    private OutroContatoController controller;

    private MockMvc mockMvc;
    private final ObjectMapper om = new ObjectMapper();
    private final UUID ID_USUARIO = UUID.randomUUID();
    private final Long ID_CONTATO = 1L;

    private UsuarioDto usuarioDto;
    private Usuario usuarioDomain;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                // Resolve o erro "No primary or single unique constructor found for interface Pageable"
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        usuarioDomain = Usuario.builder()
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

        usuarioDto = UsuarioDto.builder()
                .id(ID_USUARIO)
                .nome("nome teste")
                .telefone("00000000000")
                .senha("senhateste123")
                .email("emailteste@123")
                .telefoneConectado("00000000000")
                .atributosQualificacao(Map.of("teste", "valor_teste"))
                .configuracaoCrm(
                        ConfiguracaoCrmDto.builder()
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
    @DisplayName("Cadastrar: Deve retornar 201 Created")
    void cadastrarDeveRetornarCreated() throws Exception {
        // Arrange
        OutroContatoDto dtoInput = OutroContatoDto.builder()
                .nome("Contato Teste")
                .telefone("11999999999")
                .tipoContato(TipoContato.PADRAO)
                .usuario(usuarioDto)
                .build();

        OutroContato domainRetorno = OutroContato.builder()
                .id(ID_CONTATO)
                .nome("Contato Teste")
                .telefone("11999999999")
                .tipoContato(TipoContato.PADRAO)
                .usuario(usuarioDomain)
                .build();

        when(useCase.cadastrar(any(OutroContato.class))).thenReturn(domainRetorno);

        // Act & Assert
        mockMvc.perform(post("/outros-contatos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dtoInput)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/outros-contatos/" + ID_CONTATO))
                .andExpect(jsonPath("$.dado.id").value(ID_CONTATO))
                .andExpect(jsonPath("$.dado.nome").value("Contato Teste"));
    }

    @Test
    @DisplayName("Listar: Deve retornar 200 OK Paginado")
    void listarDeveRetornarOk() throws Exception {
        // Arrange
        OutroContato domain = OutroContato.builder()
                .id(ID_CONTATO)
                .nome("Contato Lista")
                .usuario(usuarioDomain)
                .build();

        // Use PageRequest para evitar erro de serialização do Jackson
        Page<OutroContato> page = new PageImpl<>(List.of(domain), PageRequest.of(0, 10), 1);

        when(useCase.listar(any(Pageable.class), eq(ID_USUARIO))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/outros-contatos/listar/{idUsuario}", ID_USUARIO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.content[0].id").value(ID_CONTATO))
                .andExpect(jsonPath("$.dado.content[0].nome").value("Contato Lista"));
    }

    @Test
    @DisplayName("Alterar: Deve retornar 200 OK")
    void alterarDeveRetornarOk() throws Exception {
        // Arrange
        OutroContatoDto dtoInput = OutroContatoDto.builder()
                .nome("Nome Alterado")
                .usuario(usuarioDto)
                .build();

        OutroContato domainRetorno = OutroContato.builder()
                .id(ID_CONTATO)
                .nome("Nome Alterado")
                .usuario(usuarioDomain)
                .build();

        when(useCase.alterar(eq(ID_CONTATO), any(OutroContato.class))).thenReturn(domainRetorno);

        // Act & Assert
        mockMvc.perform(put("/outros-contatos/{id}", ID_CONTATO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dtoInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(ID_CONTATO))
                .andExpect(jsonPath("$.dado.nome").value("Nome Alterado"));
    }

    @Test
    @DisplayName("Deletar: Deve retornar 204 No Content")
    void deletarDeveRetornarNoContent() throws Exception {
        mockMvc.perform(delete("/outros-contatos/{id}", ID_CONTATO))
                .andExpect(status().isNoContent());

        verify(useCase).deletar(ID_CONTATO);
    }
}
