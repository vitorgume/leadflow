package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.application.gateways.AgenteGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.MensagemAgenteDto;
import com.guminteligencia.ura_chatbot_ia.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgenteUseCaseTest {

    @Mock
    private AgenteGateway gateway;

    @InjectMocks
    private AgenteUseCase useCase;

    private Cliente cliente;
    private ConversaAgente conversa;
    private UUID clienteId;
    private UUID conversaId;
    private UUID idUsuario;

    @BeforeEach
    void setup() {
        clienteId = UUID.randomUUID();
        conversaId = UUID.randomUUID();
        idUsuario = UUID.randomUUID();

        cliente = Cliente.builder()
                .id(clienteId)
                .build();

        conversa = mock(ConversaAgente.class);
    }

    @Test
    void deveEnviarMensagemConcatenarListaEChamarGateway() {
        List<MensagemContexto> msgs = List.of(
                MensagemContexto.builder().mensagem("oi").audioUrl("a1").imagemUrl("i1").build(),
                MensagemContexto.builder().mensagem("tudo bem?").audioUrl("a2").imagemUrl("i2").build(),
                MensagemContexto.builder().mensagem("até logo").build()
        );
        String esperadoConcat = "oi, tudo bem?, até logo";
        MensagemAgenteDto capturado;
        when(gateway.enviarMensagem(any())).thenReturn("resp-ok");
        when(conversa.getId()).thenReturn(conversaId);

        String resposta = useCase.enviarMensagem(cliente, conversa, msgs);

        assertEquals("resp-ok", resposta);

        ArgumentCaptor<MensagemAgenteDto> dtoCap = ArgumentCaptor.forClass(MensagemAgenteDto.class);
        verify(gateway).enviarMensagem(dtoCap.capture());
        capturado = dtoCap.getValue();

        assertEquals(clienteId.toString(), capturado.getClienteId());
        assertEquals(conversaId.toString(), capturado.getConversaId());
        assertEquals(esperadoConcat, capturado.getMensagem());
        assertEquals(java.util.Arrays.asList("a1", "a2", null), capturado.getAudiosUrl());
        assertEquals(java.util.Arrays.asList("i1", "i2", null), capturado.getImagensUrl());
    }

    @Test
    void deveEnviarMensagemComListaVaziaConcatenacaoVazia() {
        when(gateway.enviarMensagem(any())).thenReturn("vazio");
        when(conversa.getId()).thenReturn(conversaId);

        String resp = useCase.enviarMensagem(cliente, conversa, List.of());

        assertEquals("vazio", resp);
        ArgumentCaptor<MensagemAgenteDto> cap = ArgumentCaptor.forClass(MensagemAgenteDto.class);
        verify(gateway).enviarMensagem(cap.capture());
        assertEquals("", cap.getValue().getMensagem());
    }

    @Test
    void deveLancarRuntimeExceptionQuandoGatewayRetornarJsonInvalidoEmTransformacao() {
        String texto = "qualquer texto";
        when(gateway.enviarJsonTrasformacao(texto, idUsuario)).thenReturn("not a json");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.enviarJsonTrasformacao(texto, idUsuario));
        assertTrue(ex.getMessage().contains("Erro ao tentar mapear JSON da IA"));
    }

    @Test
    void deveRetornarQualificacaoQuandoJsonValido() throws Exception {
        String texto = "texto";
        Qualificacao qual = new Qualificacao();
        qual.setNome("Ana");
        String json = new ObjectMapper().writeValueAsString(qual);

        when(gateway.enviarJsonTrasformacao(texto, idUsuario)).thenReturn(json);

        Qualificacao resultado = useCase.enviarJsonTrasformacao(texto, idUsuario);

        assertNotNull(resultado);
        assertEquals("Ana", resultado.getNome());
    }

    @Test
    void deveRetornarQualificacaoQuandoGatewayRetornaJsonDentroDeStringTextual() throws Exception {
        String texto = "texto";

        // Monta um JSON válido como string
        Qualificacao qual = new Qualificacao();
        qual.setNome("Ana");

        // JSON "normal"
        String innerJson = new ObjectMapper().writeValueAsString(qual);
        // Agora embrulha como STRING JSON (nó textual): "\"{...}\""
        String wrappedAsTextNode = new ObjectMapper().writeValueAsString(innerJson);

        when(gateway.enviarJsonTrasformacao(texto, idUsuario)).thenReturn(wrappedAsTextNode);

        Qualificacao resultado = useCase.enviarJsonTrasformacao(texto, idUsuario);

        assertNotNull(resultado);
        assertEquals("Ana", resultado.getNome());
    }

    @Test
    void deveLancarRuntimeExceptionQuandoJsonTextualTemConteudoInternoInvalido() throws Exception {
        String texto = "qualquer";

        // Primeiro parse vira nó textual: "\"not a json\""
        String wrappedInvalid = new ObjectMapper().writeValueAsString("not a json");

        when(gateway.enviarJsonTrasformacao(texto, idUsuario)).thenReturn(wrappedInvalid);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.enviarJsonTrasformacao(texto, idUsuario));

        assertTrue(ex.getMessage().contains("Erro ao tentar mapear JSON da IA"));
    }
}
