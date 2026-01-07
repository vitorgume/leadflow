package com.guminteligencia.ura_chatbot_ia.infrastructure.security.jwt.jwtAuthFilterTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.application.gateways.MensageriaGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.UsuarioUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
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

        String json = objectMapper.writeValueAsString(UsuarioDto.builder()
                .nome("Nome teste")
                .senha("senha123")
                .telefone("email123@gmail.com")
                .build()
        );


        String token = jwtUtil.generateToken("user1");

        Mockito.when(usuarioUseCase.cadastrar(Mockito.any())).thenReturn(new Usuario());

        mockMvc.perform(post("/usuarios")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated());
    }
}