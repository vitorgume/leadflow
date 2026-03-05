package com.gumeinteligencia.api_intermidiaria.application.usecase.validadorMensagens;

import static org.junit.jupiter.api.Assertions.*;

import com.gumeinteligencia.api_intermidiaria.application.usecase.UsuarioUseCase;
import com.gumeinteligencia.api_intermidiaria.domain.Mensagem;
import com.gumeinteligencia.api_intermidiaria.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidadorSoftwareLigadoTest {

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @InjectMocks
    private ValidadorSoftwareLigado validador;

    private Mensagem mensagemMock;
    private Usuario usuarioMock;
    private final String TELEFONE_CONECTADO = "5511999999999";

    @BeforeEach
    void setup() {
        mensagemMock = mock(Mensagem.class);
        usuarioMock = mock(Usuario.class);

        // Ensina o mock da mensagem a devolver o telefone quando for perguntado
        when(mensagemMock.getTelefoneConectado()).thenReturn(TELEFONE_CONECTADO);
    }

    @Test
    @DisplayName("Deve retornar TRUE (ignorar) quando o software estiver DESLIGADO")
    void deveIgnorarMensagemQuandoSoftwareEstiverDesligado() {
        // Prepara
        when(usuarioMock.getSoftwareLigado()).thenReturn(false); // 👈 Software OFF
        when(usuarioUseCase.consultarPorTelefoneConectado(TELEFONE_CONECTADO)).thenReturn(usuarioMock);

        // Executa
        boolean resultado = validador.deveIgnorar(mensagemMock);

        // Verifica
        assertTrue(resultado, "Deveria retornar TRUE para ignorar a mensagem");
        verify(usuarioUseCase, times(1)).consultarPorTelefoneConectado(TELEFONE_CONECTADO);
    }

    @Test
    @DisplayName("Deve retornar FALSE (não ignorar) quando o software estiver LIGADO")
    void naoDeveIgnorarMensagemQuandoSoftwareEstiverLigado() {
        // Prepara
        when(usuarioMock.getSoftwareLigado()).thenReturn(true); // 👈 Software ON
        when(usuarioUseCase.consultarPorTelefoneConectado(TELEFONE_CONECTADO)).thenReturn(usuarioMock);

        // Executa
        boolean resultado = validador.deveIgnorar(mensagemMock);

        // Verifica
        assertFalse(resultado, "Deveria retornar FALSE para deixar a mensagem passar");
        verify(usuarioUseCase, times(1)).consultarPorTelefoneConectado(TELEFONE_CONECTADO);
    }
}