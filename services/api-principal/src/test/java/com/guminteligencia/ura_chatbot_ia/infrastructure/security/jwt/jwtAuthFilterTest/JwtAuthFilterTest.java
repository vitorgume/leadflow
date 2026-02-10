package com.guminteligencia.ura_chatbot_ia.infrastructure.security.jwt.jwtAuthFilterTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.application.gateways.MensageriaGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.UsuarioUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ConfiguracaoCrmDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.UsuarioDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.security.jwt.JwtUtil;
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

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("it")
class JwtAuthFilterTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MensageriaGateway mensageriaGateway;

    @MockitoBean
    private UsuarioUseCase usuarioUseCase;

    @MockitoBean
    private RetryBackoffSpec retryBackoffSpec;

    @Test
    void securedEndpoint_withoutToken_forbidden() throws Exception {
        mockMvc.perform(get("/algum-endpoint-protegido"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void securedEndpoint_withValidToken_ok() throws Exception {

        String json = objectMapper.writeValueAsString(
                UsuarioDto.builder()
                        .id(UUID.randomUUID())
                        .nome("nome teste")
                        .telefone("00000000000")
                        .senha("senhateste123")
                        .email("emailteste@123")
                        .telefoneConectado("00000000000")
                        // --- CORREÇÃO: Trocamos new Object() por uma String ---
                        .atributosQualificacao(Map.of("teste", "valor_teste"))
                        // -----------------------------------------------------
                        .configuracaoCrm(
                                ConfiguracaoCrmDto.builder()
                                        .crmType(CrmType.KOMMO)
                                        .mapeamentoCampos(Map.of("teste", "teste"))
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
        );

        Usuario usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("nome teste")
                .telefone("00000000000")
                .senha("senhateste123")
                .email("emailteste@123")
                .telefoneConectado("00000000000")
                // --- CORREÇÃO: Trocamos new Object() por uma String ---
                .atributosQualificacao(Map.of("teste", "valor_teste"))
                // -----------------------------------------------------
                .configuracaoCrm(
                        ConfiguracaoCrm.builder()
                                .crmType(CrmType.KOMMO)
                                .mapeamentoCampos(Map.of("teste", "teste"))
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

        String token = jwtUtil.generateToken("user1");

        // Mock do useCase
        Mockito.when(usuarioUseCase.cadastrar(Mockito.any())).thenReturn(usuario);

        mockMvc.perform(post("/usuarios/cadastro")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated());
    }
}