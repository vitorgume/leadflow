package com.gumeinteligencia.api_intermidiaria.application.usecase;

import com.gumeinteligencia.api_intermidiaria.application.usecase.validadorMensagens.ValidadorMensagemUseCase;
import com.gumeinteligencia.api_intermidiaria.domain.Cliente;
import com.gumeinteligencia.api_intermidiaria.domain.Contexto;
import com.gumeinteligencia.api_intermidiaria.domain.Mensagem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessarMensagemUseCaseTest {

    @Mock
    private ValidadorMensagemUseCase validadorMensagem;

    @Mock
    private ContextoUseCase contextoUseCase;

    @Mock
    private ClienteUseCase clienteUseCase;

    @Mock
    private MidiaUseCase midiaUseCase;

    @InjectMocks
    private ProcessarMensagemUseCase processarMensagemUseCase;

    private Mensagem mensagem;

    @BeforeEach
    void setUp() {
        mensagem = Mensagem.builder()
                .telefone("44999999999")
                .mensagem("Olá")
                .build();
    }

    @Test
    void deveIgnorarMensagemSeValidadorRetornarTrue() {
        when(validadorMensagem.deveIngorar(mensagem)).thenReturn(true);

        processarMensagemUseCase.processarNovaMensagem(mensagem);

        verify(contextoUseCase, never()).consultarPorTelefoneAtivo(any());
        verify(contextoUseCase, never()).processarContextoExistente(any(), any());
        verify(contextoUseCase, never()).iniciarNovoContexto(any());
        verifyNoInteractions(clienteUseCase);
    }

    @Test
    void deveProcessarMensagemComContextoExistente() {
        Contexto contexto = Contexto.builder().telefone("44999999999").build();

        when(validadorMensagem.deveIngorar(mensagem)).thenReturn(false);
        when(contextoUseCase.consultarPorTelefoneAtivo(mensagem.getTelefone()))
                .thenReturn(Optional.of(contexto));

        when(midiaUseCase.extrairMidias(Mockito.any())).thenReturn(mensagem);

        processarMensagemUseCase.processarNovaMensagem(mensagem);

        verify(contextoUseCase).processarContextoExistente(contexto, mensagem);
        verify(contextoUseCase, never()).iniciarNovoContexto(any());
        verifyNoInteractions(clienteUseCase);
    }

    @Test
    void deveIniciarNovoContexto_quandoSemContextoEClienteDoCanalChatbot() {
        // cliente presente com canal código 0 (CHATBOT)
        Cliente clienteChatbot = Cliente.builder()
                .id(UUID.randomUUID())
                .build();

        when(validadorMensagem.deveIngorar(mensagem)).thenReturn(false);
        when(contextoUseCase.consultarPorTelefoneAtivo(mensagem.getTelefone()))
                .thenReturn(Optional.empty());
        when(clienteUseCase.consultarPorTelefone(mensagem.getTelefone()))
                .thenReturn(Optional.of(clienteChatbot));

        when(midiaUseCase.extrairMidias(Mockito.any())).thenReturn(mensagem);

        processarMensagemUseCase.processarNovaMensagem(mensagem);

        verify(contextoUseCase).iniciarNovoContexto(mensagem);
        verify(contextoUseCase, never()).processarContextoExistente(any(), any());
    }

    @Test
    void deveEnviarParaUra_quandoSemContextoEClienteDoCanalUra() {
        // cliente presente com canal URA (código ≠ 0)
        Cliente clienteUra = Cliente.builder()
                .id(UUID.randomUUID())
                .build();

        when(validadorMensagem.deveIngorar(mensagem)).thenReturn(false);
        when(contextoUseCase.consultarPorTelefoneAtivo(mensagem.getTelefone()))
                .thenReturn(Optional.empty());
        when(clienteUseCase.consultarPorTelefone(mensagem.getTelefone()))
                .thenReturn(Optional.of(clienteUra));

        when(midiaUseCase.extrairMidias(Mockito.any())).thenReturn(mensagem);

        processarMensagemUseCase.processarNovaMensagem(mensagem);

        verify(contextoUseCase, never()).iniciarNovoContexto(any());
        verify(contextoUseCase, never()).processarContextoExistente(any(), any());
    }

    @Test
    void deveSeguirRoteador_quandoSemContextoEClienteInexistente_usarChatbotTrue() {
        when(validadorMensagem.deveIngorar(mensagem)).thenReturn(false);
        when(contextoUseCase.consultarPorTelefoneAtivo(mensagem.getTelefone()))
                .thenReturn(Optional.empty());
        when(clienteUseCase.consultarPorTelefone(mensagem.getTelefone()))
                .thenReturn(Optional.empty());

        when(midiaUseCase.extrairMidias(Mockito.any())).thenReturn(mensagem);

        processarMensagemUseCase.processarNovaMensagem(mensagem);

        verify(contextoUseCase).iniciarNovoContexto(mensagem);
    }

    @Test
    void deveSeguirRoteador_quandoSemContextoEClienteInexistente_usarChatbotFalse() {
        when(validadorMensagem.deveIngorar(mensagem)).thenReturn(false);
        when(contextoUseCase.consultarPorTelefoneAtivo(mensagem.getTelefone()))
                .thenReturn(Optional.empty());
        when(clienteUseCase.consultarPorTelefone(mensagem.getTelefone()))
                .thenReturn(Optional.empty());

        when(midiaUseCase.extrairMidias(Mockito.any())).thenReturn(mensagem);

        processarMensagemUseCase.processarNovaMensagem(mensagem);

        verify(contextoUseCase, never()).iniciarNovoContexto(any());
    }
}