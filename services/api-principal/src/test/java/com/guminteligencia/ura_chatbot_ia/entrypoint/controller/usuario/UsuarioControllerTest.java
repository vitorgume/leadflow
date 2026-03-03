package com.guminteligencia.ura_chatbot_ia.entrypoint.controller.usuario;

import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.UsuarioRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConfiguracaoCrmEntity;
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

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false"
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Tag("it")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioRepository repository;

    private final UUID ID_USUARIO = UUID.randomUUID();

    private UsuarioEntity usuarioEntity;

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
    }

    @Test
    @DisplayName("Cadastrar: Quando sucesso retorna Created")
    void cadastrarQuandoSucessoRetornaCreated() throws Exception {
        // Arrange
        // 1. Configura a entidade completa que o banco vai "retornar"
        UsuarioEntity saved = UsuarioEntity.builder()
                .id(ID_USUARIO)
                .nome("Novo Usuario")
                .telefone("5511999999999")
                .senha("senhaForte123")
                .email("teste@email.com")
                .telefoneConectado("5511888888888")
                .atributosQualificacao(Map.of("perfil", "admin")) // Map simples
                .configuracaoCrm(ConfiguracaoCrmEntity.builder() // Objeto aninhado
                        .crmType(CrmType.KOMMO)
                        .acessToken("token-crm-xyz")
                        .mapeamentoCampos(Map.of("origem", "destino"))
                        .build())
                .mensagemDirecionamentoVendedor("Aguarde um momento")
                .mensagemRecontatoG1("Olá novamente")
                .whatsappToken("wpp-token-123")
                .whatsappIdInstance("instance-01")
                .agenteApiKey("ai-key-999")
                .build();

        // 2. Mocks
        given(repository.findByEmail("teste@email.com")).willReturn(Optional.empty());
        given(repository.save(any(UsuarioEntity.class))).willReturn(saved);

        // 3. JSON Completo
        // Nota: ConfiguracaoCrmDto e Maps devem ser objetos JSON válidos
        String json = """
            {
              "nome": "Novo Usuario",
              "telefone": "5511999999999",
              "senha": "senhaForte123",
              "email": "teste@email.com",
              "telefone_conectado": "5511888888888",
              "atributos_qualificacao": {
                "perfil": "admin"
              },
              "configuracao_crm": {
                "crm_type": "KOMMO",
                "acess_token": "token-crm-xyz",
                "mapeamento_campos": {
                    "origem": "destino"
                }
              },
              "mensagem_direcionamento_vendedor": "Aguarde um momento",
              "mensagem_recontato_g1": "Olá novamente",
              "whatsapp_token": "wpp-token-123",
              "whatsapp_id_instance": "instance-01",
              "agente_api_key": "ai-key-999"
            }
        """;

        // Act & Assert
        mockMvc.perform(post("/usuarios/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/usuarios/cadastro/" + ID_USUARIO))
                // Validações básicas
                .andExpect(jsonPath("$.dado.id").value(ID_USUARIO.toString()))
                .andExpect(jsonPath("$.dado.email").value("teste@email.com"))
                // Validações dos novos campos (Snake Case no JSON de retorno também)
                .andExpect(jsonPath("$.dado.telefone_conectado").value("5511888888888"));
    }

    @Test
    @DisplayName("ConsultarPorId: Quando sucesso retorna OK")
    void consultarPorIdQuandoSucessoRetornaOk() throws Exception {
        given(repository.findById(ID_USUARIO)).willReturn(Optional.of(usuarioEntity));

        // Act & Assert
        mockMvc.perform(get("/usuarios/" + ID_USUARIO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(ID_USUARIO.toString()))
                .andExpect(jsonPath("$.dado.nome").value("nome teste"));
    }

    @Test
    @DisplayName("Deletar: Quando sucesso retorna No Content")
    void deletarQuandoSucessoRetornaNoContent() throws Exception {
        // Arrange
        // Assume-se que o UseCase verifica a existência antes de deletar
        given(repository.findById(ID_USUARIO)).willReturn(Optional.of(usuarioEntity));

        // Act
        mockMvc.perform(delete("/usuarios/" + ID_USUARIO))
                .andExpect(status().isNoContent());

        // Assert
        Mockito.verify(repository).deleteById(ID_USUARIO);
    }

    @Test
    @DisplayName("Alterar: Quando sucesso retorna OK")
    void alterarQuandoSucessoRetornaOk() throws Exception {
        // Arrange
        UsuarioEntity saved = UsuarioEntity.builder()
                .id(ID_USUARIO)
                .nome("Nome Alterado")
                .telefone("5511999999999")
                .senha("senhaForte123")
                .email("teste@email.com")
                .telefoneConectado("5511888888888")
                .configuracaoCrm(ConfiguracaoCrmEntity.builder()
                        .crmType(CrmType.KOMMO)
                        .acessToken("token-crm-xyz")
                        .build())
                .build();

        // 1. O UseCase vai primeiro procurar se o usuário existe para poder alterá-lo
        given(repository.findById(ID_USUARIO)).willReturn(Optional.of(usuarioEntity));

        // 2. O UseCase vai tentar salvar o usuário com as informações atualizadas e criptografadas
        given(repository.save(any(UsuarioEntity.class))).willReturn(saved);

        String json = """
            {
              "nome": "Nome Alterado",
              "telefone": "5511999999999",
              "senha": "senhaForte123",
              "email": "teste@email.com",
              "telefone_conectado": "5511888888888",
              "configuracao_crm": {
                "crm_type": "KOMMO",
                "acess_token": "token-crm-xyz"
              },
              "whatsapp_token": "wpp-token-123",
              "whatsapp_id_instance": "instance-01",
              "agente_api_key": "ai-key-999"
            }
        """;

        // Act & Assert
        mockMvc.perform(put("/usuarios/" + ID_USUARIO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(ID_USUARIO.toString()))
                .andExpect(jsonPath("$.dado.nome").value("Nome Alterado"));
    }

}