package com.guminteligencia.ura_chatbot_ia.entrypoint.controller.outroContato;

import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import com.guminteligencia.ura_chatbot_ia.domain.TipoContato;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.OutroContatoRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.UsuarioRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConfiguracaoCrmEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.OutroContatoEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.UsuarioEntity;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
class OutroContatoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OutroContatoRepository repository;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    private final UUID ID_USUARIO = UUID.randomUUID();
    private final Long ID_CONTATO = 1L;

    private UsuarioEntity usuarioEntity;

    @BeforeEach
    void setUp() {
        usuarioEntity = UsuarioEntity.builder()
                .id(UUID.randomUUID())
                .nome("nome teste")
                .telefone("00000000000")
                .senha("senhateste123")
                .email("emailteste@123")
                .telefoneConectado("00000000000")
                .atributosQualificacao(Map.of("teste", "teste value"))
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
        // 1. Mock do Usuário (Obrigatório para o UseCase)
        given(usuarioRepository.findById(ID_USUARIO)).willReturn(Optional.of(usuarioEntity));

        // 2. Mock das Validações de Duplicidade (Devem retornar vazio para permitir cadastro)
        // Validação por Tipo
        given(repository.findByTipoContatoAndUsuario_Id(eq(TipoContato.PADRAO), eq(ID_USUARIO)))
                .willReturn(Optional.empty());
        // Validação por Telefone
        given(repository.findByTelefone("11999999999")).willReturn(Optional.empty());

        // 3. Mock do Save
        OutroContatoEntity saved = OutroContatoEntity.builder()
                .id(ID_CONTATO)
                .nome("Novo Contato")
                .telefone("11999999999")
                .tipoContato(TipoContato.PADRAO)
                .usuario(usuarioEntity)
                .build();

        given(repository.save(any(OutroContatoEntity.class))).willReturn(saved);

        String json = """
            {
              "nome": "Novo Contato",
              "telefone": "11999999999",
              "tipo_contato": "PADRAO",
              "usuario": { "id": "%s" }
            }
        """.formatted(ID_USUARIO);

        // Act & Assert
        mockMvc.perform(post("/outros-contatos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/outros-contatos/" + ID_CONTATO))
                .andExpect(jsonPath("$.dado.id").value(ID_CONTATO));
    }

    @Test
    @DisplayName("Listar: Quando sucesso retorna OK")
    void listarQuandoSucessoRetornaOk() throws Exception {
        // Arrange
        OutroContatoEntity entity = OutroContatoEntity.builder()
                .id(ID_CONTATO)
                .nome("Contato Lista")
                .usuario(usuarioEntity)
                .build();

        Page<OutroContatoEntity> page = new PageImpl<>(List.of(entity));

        given(repository.findByUsuario_Id(any(Pageable.class), eq(ID_USUARIO)))
                .willReturn(page);

        // Act & Assert
        mockMvc.perform(get("/outros-contatos/listar/" + ID_USUARIO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.content[0].id").value(ID_CONTATO));
    }

    @Test
    @DisplayName("Alterar: Quando sucesso retorna OK")
    void alterarQuandoSucessoRetornaOk() throws Exception {
        OutroContatoEntity existing = OutroContatoEntity.builder()
                .id(ID_CONTATO)
                .nome("Antigo")
                .usuario(usuarioEntity)
                .build();

        // UseCase costuma buscar antes de alterar (verifique sua impl, assumindo que sim)
        given(repository.findById(ID_CONTATO)).willReturn(Optional.of(existing));

        // Mock das validações (caso o alterar também valide telefone/tipo)
        // Assumindo que o alterar não troca o tipo/telefone neste teste simples

        OutroContatoEntity updated = OutroContatoEntity.builder()
                .id(ID_CONTATO)
                .nome("Novo Nome")
                .usuario(usuarioEntity)
                .build();

        given(repository.save(any(OutroContatoEntity.class))).willReturn(updated);

        String json = """
            {
              "nome": "Novo Nome",
              "usuario": { "id": "%s" }
            }
        """.formatted(ID_USUARIO);

        // Act & Assert
        mockMvc.perform(put("/outros-contatos/" + ID_CONTATO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(ID_CONTATO))
                .andExpect(jsonPath("$.dado.nome").value("Novo Nome"));
    }

    @Test
    @DisplayName("Deletar: Quando sucesso retorna No Content")
    void deletarQuandoSucessoRetornaNoContent() throws Exception {

        OutroContatoEntity existing = OutroContatoEntity.builder()
                .id(ID_CONTATO)
                .nome("Antigo")
                .usuario(usuarioEntity)
                .build();

        // Arrange (Assume-se que o UseCase valida existência)
        given(repository.findById(ID_CONTATO)).willReturn(Optional.of(existing));

        // Act
        mockMvc.perform(delete("/outros-contatos/" + ID_CONTATO))
                .andExpect(status().isNoContent());

        // Assert
        Mockito.verify(repository).deleteById(ID_CONTATO);
    }

}