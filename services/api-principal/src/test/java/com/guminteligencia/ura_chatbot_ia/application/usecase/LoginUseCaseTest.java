package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.CredenciasIncorretasException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.LoginGateway;
import com.guminteligencia.ura_chatbot_ia.domain.LoginResponse;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @Mock
    private LoginGateway loginGateway;

    @Mock
    private CriptografiaUseCase criptografiaUseCase;

    @InjectMocks
    private LoginUseCase useCase;

    private final String email = "admin@example.com";
    private final String senhaBruta = "senha123";
    private final String senhaHash = "HASHED";
    private final UUID adminId = UUID.randomUUID();

    private Usuario usuario;

    @BeforeEach
    void setup() {
        usuario = Usuario.builder()
                .id(adminId)
                .telefone(email)
                .senha(senhaHash)
                .email(email) // Set email here
                .build();
    }

    @Test
    void deveAutenticarComCredenciaisValidas() {
        when(usuarioUseCase.consultarPorEmail(email)).thenReturn(usuario);
        when(criptografiaUseCase.validaSenha(senhaBruta, senhaHash)).thenReturn(true);
        when(loginGateway.gerarToken(email)).thenReturn("TOKEN123");

        LoginResponse response = useCase.autenticar(email, senhaBruta);

        assertEquals(adminId, response.getId(), "Deve retornar o ID do administrador");
        assertEquals("TOKEN123", response.getToken(), "Deve retornar o token gerado pelo gateway");

        verify(usuarioUseCase).consultarPorEmail(email);
        verify(criptografiaUseCase).validaSenha(senhaBruta, senhaHash);
        verify(loginGateway).gerarToken(email);
    }

    @Test
    void deveLancarCredenciasIncorretasQuandoEmailDiferente() {
        Usuario outro = Usuario.builder()
                .id(adminId)
                .telefone("outro@example.com")
                .senha(senhaHash)
                .email("outro@example.com") // Set email here
                .build();
        when(usuarioUseCase.consultarPorEmail(email)).thenReturn(outro);

        assertThrows(
                CredenciasIncorretasException.class,
                () -> useCase.autenticar(email, senhaBruta),
                "Quando o email do administrador não coincidir, deve lançar CredenciasIncorretasException"
        );

        verify(usuarioUseCase).consultarPorEmail(email);
        verifyNoInteractions(criptografiaUseCase, loginGateway);
    }

    @Test
    void deveLancarCredenciasIncorretasQuandoSenhaInvalida() {
        when(usuarioUseCase.consultarPorEmail(email)).thenReturn(usuario);
        when(criptografiaUseCase.validaSenha(senhaBruta, senhaHash)).thenReturn(false);

        assertThrows(
                CredenciasIncorretasException.class,
                () -> useCase.autenticar(email, senhaBruta),
                "Quando a senha for inválida, deve lançar CredenciasIncorretasException"
        );

        verify(usuarioUseCase).consultarPorEmail(email);
        verify(criptografiaUseCase).validaSenha(senhaBruta, senhaHash);
        verifyNoInteractions(loginGateway);
    }
}