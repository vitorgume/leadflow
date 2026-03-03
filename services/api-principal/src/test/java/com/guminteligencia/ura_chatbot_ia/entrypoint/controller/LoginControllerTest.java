package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.CredenciasIncorretasException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.LoginGateway;
import com.guminteligencia.ura_chatbot_ia.application.gateways.MensageriaGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.CriptografiaUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.LoginUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.UsuarioUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import com.guminteligencia.ura_chatbot_ia.domain.LoginResponse;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import reactor.util.retry.RetryBackoffSpec;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false"
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Tag("it")
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoginUseCase loginUseCase;

    @MockitoBean
    private LoginGateway loginGateway;

    @MockitoBean
    private MensageriaGateway mensageriaGateway;

    @MockitoBean
    private RetryBackoffSpec retryBackoffSpec;

    @MockitoBean
    private UsuarioUseCase usuarioUseCase;

    @MockitoBean
    private CriptografiaUseCase criptografiaUseCase;

    private Usuario usuario;
    private final UUID ID_USUARIO = UUID.randomUUID();

    @BeforeEach
    void setMockMensageria() {
        given(mensageriaGateway.listarAvisos())
                .willReturn(List.of());

        usuario = Usuario.builder()
                .id(ID_USUARIO)
                .nome("nome teste")
                .telefone("+5511999000111")
                .senha("senha123")
                .email("emailteste123@teste")
                .telefoneConectado("00000000000")
                .atributosQualificacao(Map.of("teste", new Object()))
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
    void logarComCredenciaisValidasRetornaCreatedEBodyCorreto() throws Exception {
        String payload = """
                    { "email": "emailteste123@teste", "senha": "senha123" }
                """;

        // 1. Criar a resposta esperada
        LoginResponse responseEsperado = LoginResponse.builder()
                .token("meu-token-abc")
                .id(ID_USUARIO)
                .build();

        // 2. Mockar DIRETAMENTE o UseCase (que é quem o Controller chama)
        // Note que não precisamos mockar usuarioUseCase nem loginGateway aqui,
        // pois o código real do LoginUseCase não vai rodar.
        given(loginUseCase.autenticar(Mockito.anyString(), Mockito.anyString()))
                .willReturn(responseEsperado);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.token").doesNotExist())
                .andExpect(jsonPath("$.dado.id").value(ID_USUARIO.toString()))
                .andExpect(cookie().exists("jwt_token"))
                .andExpect(cookie().httpOnly("jwt_token", true));
    }


    @Test
    void logarComCredenciaisInvalidasRetornaUnauthorized() throws Exception {
        String telefone = "emailteste123@teste";
        String payload = """
                    { "email": "%s", "senha": "senhaErrada" }
                """.formatted(telefone);

        given(loginUseCase.autenticar(telefone, "senhaErrada"))
                .willThrow(new CredenciasIncorretasException());

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized());
    }


}
