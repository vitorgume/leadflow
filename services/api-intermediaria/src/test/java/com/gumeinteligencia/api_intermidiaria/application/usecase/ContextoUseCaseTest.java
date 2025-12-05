package com.gumeinteligencia.api_intermidiaria.application.usecase;

import com.gumeinteligencia.api_intermidiaria.application.gateways.ContextoGateway;
import com.gumeinteligencia.api_intermidiaria.application.gateways.MensageriaGateway;
import com.gumeinteligencia.api_intermidiaria.domain.AvisoContexto;
import com.gumeinteligencia.api_intermidiaria.domain.Contexto;
import com.gumeinteligencia.api_intermidiaria.domain.Mensagem;
import com.gumeinteligencia.api_intermidiaria.domain.MensagemContexto;
import com.gumeinteligencia.api_intermidiaria.domain.StatusContexto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContextoUseCaseTest {

    @Mock
    private ContextoGateway gateway;

    @Mock
    private MensageriaGateway mensageriaGateway;

    @Mock
    private AvisoContextoUseCase avisoContextoUseCase;

    @InjectMocks
    private ContextoUseCase contextoUseCase;

    private final String telefone = "44999999999";
    private Mensagem mensagem;

    @BeforeEach
    void setUp() {
        mensagem = Mensagem.builder()
                .telefone(telefone)
                .mensagem("Oi, tudo bem?")
                .urlImagem("http://img")
                .urlAudio("http://audio")
                .build();
    }

    @Test
    void deveConsultarContextoPorTelefone() {
        Contexto contexto = Contexto.builder().telefone(telefone).status(StatusContexto.ATIVO).build();
        when(gateway.consultarPorTelefone(telefone)).thenReturn(Optional.of(contexto));

        Optional<Contexto> resultado = contextoUseCase.consultarPorTelefone(telefone);

        assertTrue(resultado.isPresent());
        assertEquals(telefone, resultado.get().getTelefone());
        assertEquals(StatusContexto.ATIVO, resultado.get().getStatus());
    }

    @Test
    void deveProcessarContextoExistenteEAcrescentarMensagem() {
        List<MensagemContexto> mensagens = new ArrayList<>();
        mensagens.add(MensagemContexto.builder().mensagem("Mensagem antiga").build());

        Contexto contexto = Contexto.builder()
                .id(UUID.randomUUID())
                .telefone(telefone)
                .status(null)
                .mensagens(mensagens)
                .build();

        when(gateway.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        contextoUseCase.processarContextoExistente(contexto, mensagem);

        ArgumentCaptor<Contexto> captor = ArgumentCaptor.forClass(Contexto.class);
        verify(gateway).salvar(captor.capture());
        Contexto atualizado = captor.getValue();

        assertEquals(StatusContexto.ATIVO, atualizado.getStatus());
        assertEquals(2, atualizado.getMensagens().size());
        MensagemContexto ultima = atualizado.getMensagens().get(1);
        assertEquals(mensagem.getMensagem(), ultima.getMensagem());
        assertEquals(mensagem.getUrlImagem(), ultima.getImagemUrl());
        assertEquals(mensagem.getUrlAudio(), ultima.getAudioUrl());
        verify(mensageriaGateway, never()).enviarParaFila(any());
    }

    @Test
    void deveProcessarContextoSemMensagensMantendoStatusExistente() {
        Contexto contexto = Contexto.builder()
                .id(UUID.randomUUID())
                .telefone(telefone)
                .status(StatusContexto.OBSOLETO)
                .build();

        when(gateway.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        contextoUseCase.processarContextoExistente(contexto, mensagem);

        ArgumentCaptor<Contexto> captor = ArgumentCaptor.forClass(Contexto.class);
        verify(gateway).salvar(captor.capture());

        Contexto atualizado = captor.getValue();
        assertEquals(StatusContexto.OBSOLETO, atualizado.getStatus());
        assertEquals(1, atualizado.getMensagens().size());
        MensagemContexto novaMensagem = atualizado.getMensagens().get(0);
        assertEquals(mensagem.getMensagem(), novaMensagem.getMensagem());
        assertEquals(mensagem.getUrlImagem(), novaMensagem.getImagemUrl());
        assertEquals(mensagem.getUrlAudio(), novaMensagem.getAudioUrl());
    }

    @Test
    void deveIniciarNovoContextoEAvisarFila() {
        when(gateway.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(avisoContextoUseCase.criarAviso(any())).thenAnswer(invocation -> AvisoContexto.builder()
                .id(UUID.randomUUID())
                .idContexto(invocation.getArgument(0))
                .build());

        contextoUseCase.iniciarNovoContexto(mensagem);

        ArgumentCaptor<Contexto> captorContexto = ArgumentCaptor.forClass(Contexto.class);
        ArgumentCaptor<AvisoContexto> captorAviso = ArgumentCaptor.forClass(AvisoContexto.class);

        verify(gateway).salvar(captorContexto.capture());
        verify(avisoContextoUseCase).criarAviso(captorContexto.getValue().getId());
        verify(mensageriaGateway).enviarParaFila(captorAviso.capture());

        Contexto contextoCriado = captorContexto.getValue();
        assertEquals(telefone, contextoCriado.getTelefone());
        assertEquals(StatusContexto.ATIVO, contextoCriado.getStatus());
        List<MensagemContexto> mensagensCriadas = contextoCriado.getMensagens();
        assertEquals(1, mensagensCriadas.size());
        assertEquals(mensagem.getMensagem(), mensagensCriadas.get(0).getMensagem());
        assertEquals(mensagem.getUrlImagem(), mensagensCriadas.get(0).getImagemUrl());
        assertEquals(mensagem.getUrlAudio(), mensagensCriadas.get(0).getAudioUrl());
        assertEquals(contextoCriado.getId(), captorAviso.getValue().getIdContexto());
    }
}
