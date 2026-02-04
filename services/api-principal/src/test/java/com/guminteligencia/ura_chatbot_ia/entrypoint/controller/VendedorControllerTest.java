package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.application.gateways.MensageriaGateway;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.UsuarioRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.VendedorRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConfiguracaoCrmEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.UsuarioEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.VendedorEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import reactor.util.retry.RetryBackoffSpec;
import org.springframework.http.MediaType;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false"
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Tag("it")
class VendedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VendedorRepository repository;

    @MockitoBean
    private MensageriaGateway mensageriaGateway;

    @MockitoBean
    private RetryBackoffSpec retryBackoffSpec;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    private UsuarioEntity usuarioEntity;
    private final UUID ID_USUARIO = UUID.fromString("9ab66ba8-fddd-4455-83af-245cf80cb3da");

    @BeforeEach
    void setMockMensageria() {
        given(mensageriaGateway.listarAvisos())
                .willReturn(List.of());

        usuarioEntity = UsuarioEntity.builder()
                .id(ID_USUARIO)
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
                .build();
    }
//
    @Test
    void cadastrarQuandoSucessoRetornaCreated() throws Exception {
        given(repository.findByTelefone("99999999"))
                .willReturn(Optional.empty());

        VendedorEntity saved = new VendedorEntity();
        saved.setId(1L);
        saved.setNome("Pedro");
        saved.setTelefone("99999999");
        saved.setInativo(false);
        saved.setUsuario(usuarioEntity);
        given(repository.save(any(VendedorEntity.class)))
                .willReturn(saved);



        given(usuarioRepository.findById(Mockito.any())).willReturn(Optional.of(usuarioEntity));

        String json = """
            {
              "nome":"Pedro",
              "telefone":"99999999",
              "inativo":false,
              "padrao": false,
              "id_vendedor_crm": 123,
              "usuario": {
                "id": "9ab66ba8-fddd-4455-83af-245cf80cb3da"
              }
            }
        """;

        mockMvc.perform(post("/vendedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/vendedores/1"))
                .andExpect(jsonPath("$.dado.id").value("1"))
                .andExpect(jsonPath("$.dado.nome").value("Pedro"))
                .andExpect(jsonPath("$.dado.telefone").value("99999999"))
                .andExpect(jsonPath("$.dado.inativo").value(false));
    }

    @Test
    void alterarQuandoSucessoRetornaOk() throws Exception {
        VendedorEntity existing = new VendedorEntity();
        existing.setId(2L);
        existing.setNome("A");
        existing.setTelefone("1111");
        existing.setInativo(false);
        existing.setUsuario(usuarioEntity);
        given(repository.findById(2L))
                .willReturn(Optional.of(existing));

        VendedorEntity updated = new VendedorEntity();
        updated.setId(2L);
        updated.setNome("João");
        updated.setTelefone("88888888");
        updated.setInativo(true);
        updated.setUsuario(usuarioEntity);
        given(repository.save(any(VendedorEntity.class)))
                .willReturn(updated);

        String json = """
            {
              "nome":"João",
              "telefone":"88888888",
              "inativo":true
            }
        """;

        mockMvc.perform(put("/vendedores/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value("2"))
                .andExpect(jsonPath("$.dado.nome").value("João"))
                .andExpect(jsonPath("$.dado.inativo").value(true));
    }

    @Test
    void listarQuandoSucessoRetornaOkComLista() throws Exception {
        VendedorEntity v1 = new VendedorEntity(null,"A","1111",false, null, null, usuarioEntity);
        VendedorEntity v2 = new VendedorEntity(null,"B","2222",true, null, null, usuarioEntity);
        v1.setId(1L);
        v2.setId(2L);
        given(repository.findByUsuario_Id(Mockito.any()))
                .willReturn(List.of(v1, v2));

        mockMvc.perform(get("/vendedores/" + ID_USUARIO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado[0].id").value("1"))
                .andExpect(jsonPath("$.dado[1].id").value("2"));
    }

    @Test
    void deletarQuandoSucessoRetornaNoContent() throws Exception {
        VendedorEntity existing = new VendedorEntity();
        existing.setId(2L);
        existing.setNome("A");
        existing.setTelefone("1111");
        existing.setInativo(false);
        existing.setUsuario(usuarioEntity);

        given(repository.findById(Mockito.any()))
                .willReturn(Optional.of(existing));

        mockMvc.perform(delete("/vendedores/3"))
                .andExpect(status().isNoContent());

        then(repository).should().deleteById(3L);
    }

    @Test
    void deletarQuandoNaoEncontradoRetornaInternalServerError() throws Exception {
        VendedorEntity dummy = new VendedorEntity();
        dummy.setId(4L);
        given(repository.findById(4L)).willReturn(Optional.of(dummy));

        willThrow(new RuntimeException("falha"))
                .given(repository).deleteById(4L);

        mockMvc.perform(delete("/vendedores/4"))
                .andExpect(status().isInternalServerError());
    }
}
