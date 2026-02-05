package com.guminteligencia.ura_chatbot_ia.entrypoint.controller.configuracao;

import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.ConectorLogico;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.OperadorLogico;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.CondicaoRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.ConfiguracaoEscolhaVendedorRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.UsuarioRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.VendedorRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConfiguracaoCrmEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.UsuarioEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.CondicaoEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.ConfiguracaoEscolhaVendedorEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.VendedorEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false"
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Tag("it")
class ConfiguracaoEscolhaVendedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Repositório principal (Mockado para simular o save final)
    @MockitoBean
    private ConfiguracaoEscolhaVendedorRepository repository;

    // Repositório de Usuário (Mockado pois o UseCase chama usuarioUseCase.consultarPorId)
    @MockitoBean
    private UsuarioRepository usuarioRepository;

    // Repositório de Vendedor (Mockado para garantir que o VendedorUseCase suba sem erros)
    @MockitoBean
    private VendedorRepository vendedorRepository;

    @MockitoBean
    private CondicaoRepository condicaoRepository;

    private final UUID ID_CONFIG = UUID.randomUUID();
    private final UUID ID_USUARIO = UUID.fromString("9ab66ba8-fddd-4455-83af-245cf80cb3da");

    private UsuarioEntity usuarioEntity;
    private VendedorEntity vendedorEntity;
    private CondicaoEntity condicaoEntity;

    @BeforeEach
    void setUp() {
        usuarioEntity = UsuarioEntity.builder()
                .id(ID_USUARIO)
                .nome("nome teste")
                .telefone("00000000000")
                .senha("senhateste123")
                .email("emailteste@123")
                .telefoneConectado("00000000000")
                .atributosQualificacao(Map.of("teste", "valor_teste"))
                .configuracaoCrm(
                        ConfiguracaoCrmEntity.builder()
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

        vendedorEntity = VendedorEntity.builder()
                .id(2L)
                .nome("Vendedor entity")
                .telefone("0000000000001")
                .inativo(true)
                .idVendedorCrm(123)
                .padrao(false)
                .usuario(
                        UsuarioEntity.builder()
                                .id(UUID.randomUUID())
                                .nome("nome teste")
                                .telefone("00000000000")
                                .senha("senhateste123")
                                .email("emailteste@123")
                                .telefoneConectado("00000000000")
                                .atributosQualificacao(Map.of("teste", new Object()))
                                .configuracaoCrm(
                                        ConfiguracaoCrmEntity.builder()
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
                                .build()
                )
                .build();

        condicaoEntity = CondicaoEntity.builder()
                .id(UUID.randomUUID())
                .campo("setor")
                .operadorLogico(OperadorLogico.EQUAL)
                .valor("Tecnologia")
                .conectorLogico(ConectorLogico.OR)
                .build();
    }

    @Test
    @DisplayName("Cadastrar: Quando sucesso retorna Created")
    void cadastrarQuandoSucessoRetornaCreated() throws Exception {

        // 2. Configura o comportamento do Mock do Usuário
        // O UseCase vai chamar: usuarioUseCase.consultarPorId -> repository.findById
        given(usuarioRepository.findById(ID_USUARIO)).willReturn(Optional.of(usuarioEntity));

        // 3. Prepara a entidade que será retornada após o save
        ConfiguracaoEscolhaVendedorEntity saved = ConfiguracaoEscolhaVendedorEntity.builder()
                .id(ID_CONFIG)
                .prioridade(1)
                .usuario(usuarioEntity)
                .vendedores(Collections.emptyList())
                .condicoes(Collections.emptyList())
                .build();

        // 4. Configura o comportamento do Mock do Save principal
        given(repository.save(any(ConfiguracaoEscolhaVendedorEntity.class))).willReturn(saved);

        // JSON de entrada (Note que vendedores está vazio [], então não precisamos mockar findById de vendedor)
        String json = """
            {
              "prioridade": 1,
              "usuario": {
                "id": "%s"
              },
              "vendedores": [],
              "condicoes": []
            }
        """.formatted(ID_USUARIO);

        // Act & Assert
        mockMvc.perform(post("/configuracoes-escolha-vendedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/configuracoes-escolha-vendedores/" + ID_CONFIG))
                .andExpect(jsonPath("$.dado.id").value(ID_CONFIG.toString()))
                .andExpect(jsonPath("$.dado.prioridade").value(1));
    }

    @Test
    @DisplayName("Listar: Quando sucesso retorna OK e Lista Paginada")
    void listarQuandoSucessoRetornaOkComLista() throws Exception {
        // Arrange
        ConfiguracaoEscolhaVendedorEntity entity = ConfiguracaoEscolhaVendedorEntity.builder()
                .id(ID_CONFIG)
                .prioridade(5)
                .usuario(usuarioEntity)
                .vendedores(Collections.emptyList())
                .condicoes(Collections.emptyList())
                .build();

        Page<ConfiguracaoEscolhaVendedorEntity> page = new PageImpl<>(List.of(entity));

        given(repository.findByUsuario_Id(eq(ID_USUARIO), any(Pageable.class)))
                .willReturn(page);

        // Act & Assert
        mockMvc.perform(get("/configuracoes-escolha-vendedores/listar/" + ID_USUARIO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.content[0].id").value(ID_CONFIG.toString()))
                .andExpect(jsonPath("$.dado.content[0].prioridade").value(5));
    }

    @Test
    @DisplayName("Alterar: Quando sucesso retorna OK")
    void alterarQuandoSucessoRetornaOk() throws Exception {
        // Arrange
        ConfiguracaoEscolhaVendedorEntity existing = ConfiguracaoEscolhaVendedorEntity.builder()
                .id(ID_CONFIG)
                .prioridade(1)
                .usuario(usuarioEntity)
                .vendedores(new ArrayList<>(List.of(vendedorEntity)))
                .condicoes(new ArrayList<>(List.of(condicaoEntity)))
                .build();

        given(repository.findById(ID_CONFIG)).willReturn(Optional.of(existing));

        ConfiguracaoEscolhaVendedorEntity updated = ConfiguracaoEscolhaVendedorEntity.builder()
                .id(ID_CONFIG)
                .prioridade(10) // Valor alterado
                .usuario(usuarioEntity)
                .vendedores(new ArrayList<>(List.of(vendedorEntity)))
                .condicoes(new ArrayList<>(List.of(condicaoEntity)))
                .build();

        given(repository.save(any(ConfiguracaoEscolhaVendedorEntity.class))).willReturn(updated);

        String json = """
            {
              "prioridade": 10,
              "usuario": { "id": "%s" }
            }
        """.formatted(ID_USUARIO);

        // Act & Assert
        mockMvc.perform(put("/configuracoes-escolha-vendedores/" + ID_CONFIG)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(ID_CONFIG.toString()))
                .andExpect(jsonPath("$.dado.prioridade").value(10));
    }

    @Test
    @DisplayName("Deletar: Quando sucesso retorna No Content")
    void deletarQuandoSucessoRetornaNoContent() throws Exception {
        // Arrange
        ConfiguracaoEscolhaVendedorEntity existing = ConfiguracaoEscolhaVendedorEntity.builder()
                .id(ID_CONFIG)
                .prioridade(1)
                .usuario(usuarioEntity)
                .vendedores(new ArrayList<>(List.of(vendedorEntity)))
                .condicoes(new ArrayList<>(List.of(condicaoEntity)))
                .build();

        // Mock do findById (geralmente chamado antes do delete para validar existência no DataProvider)
        given(repository.findById(ID_CONFIG)).willReturn(Optional.of(existing));

        given(condicaoRepository.findById(Mockito.any())).willReturn(Optional.of(condicaoEntity));

        // Act & Assert
        mockMvc.perform(delete("/configuracoes-escolha-vendedores/" + ID_CONFIG))
                .andExpect(status().isNoContent());

        // Verify
        Mockito.verify(repository).deleteById(ID_CONFIG);
    }

    @Test
    @DisplayName("Deletar: Quando erro interno retorna 500")
    void deletarQuandoErroRetornaInternalServerError() throws Exception {
        // Arrange
        given(repository.findById(ID_CONFIG)).willReturn(Optional.of(ConfiguracaoEscolhaVendedorEntity.builder().build()));

        // Simula erro no repository
        willThrow(new RuntimeException("Erro BD"))
                .given(repository).deleteById(ID_CONFIG);

        // Act & Assert
        mockMvc.perform(delete("/configuracoes-escolha-vendedores/" + ID_CONFIG))
                .andExpect(status().isInternalServerError());
    }
}