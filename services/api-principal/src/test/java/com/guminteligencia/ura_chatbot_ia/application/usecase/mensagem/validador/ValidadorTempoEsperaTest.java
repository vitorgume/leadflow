package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.validador;

import com.guminteligencia.ura_chatbot_ia.application.usecase.ClienteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ConversaAgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.MensageriaUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidadorTempoEsperaTest {

    @Mock
    ConversaAgenteUseCase conversaAgenteUseCase;

    @Mock
    ClienteUseCase clienteUseCase;

    @Mock
    MensageriaUseCase mensageriaUseCase;

    @InjectMocks
    ValidadorTempoEspera validadorTempoEspera;

    @Mock
    Contexto ctx;

    @Mock
    Cliente cliente;

    @Mock
    ConversaAgente conv;

    final String tel = "+5511999000111";

    private final String TELEFONE_USUARIO = "+5511999000112";

    @BeforeEach
    void setup() {

        when(ctx.getTelefone()).thenReturn(tel);
        when(ctx.getTelefoneUsuario()).thenReturn(TELEFONE_USUARIO);
    }

    @Test
    void deveRetornarFalseSeClienteNaoEncontrado() {
        when(clienteUseCase.consultarPorTelefoneEUsuario(tel, TELEFONE_USUARIO)).thenReturn(Optional.empty());
        assertTrue(validadorTempoEspera.permitirProcessar(ctx));
        verifyNoInteractions(conversaAgenteUseCase, mensageriaUseCase);
    }

    @Test
    void deveRetornaFalseConversaNaoFinalizada() {
        when(clienteUseCase.consultarPorTelefoneEUsuario(tel, TELEFONE_USUARIO)).thenReturn(Optional.of(cliente));
        when(conversaAgenteUseCase.consultarPorCliente(cliente.getId()))
                .thenReturn(conv);
        when(conv.getFinalizada()).thenReturn(false);

        assertTrue(validadorTempoEspera.permitirProcessar(ctx));
        verifyNoInteractions(mensageriaUseCase);
    }

    @Test
    void deveRetornarFalseEDeletarMensagemSeEstiverDentroDos30minutos() {
        when(clienteUseCase.consultarPorTelefoneEUsuario(tel, TELEFONE_USUARIO)).thenReturn(Optional.of(cliente));
        when(conversaAgenteUseCase.consultarPorCliente(cliente.getId()))
                .thenReturn(conv);

        LocalDateTime fixed = LocalDateTime.of(2025, 8, 1, 12, 0);

        // --- CORREÇÃO AQUI ---
        // Adicione o Mockito.CALLS_REAL_METHODS
        try (MockedStatic<LocalDateTime> mt = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {

            // Agora configuramos apenas o 'now' para retornar o valor fixo
            mt.when(LocalDateTime::now).thenReturn(fixed);

            when(conv.getFinalizada()).thenReturn(true);
            when(conv.getDataUltimaMensagem())
                    .thenReturn(fixed.minusMinutes(10));

            // O log.info vai funcionar porque os outros métodos estáticos do LocalDateTime
            // (usados pelo Logger) vão chamar a implementação real.
            assertFalse(validadorTempoEspera.permitirProcessar(ctx));
        }
    }

    @Test
    void deveRetornarTrueNaoDeletarSeMensagemDepois30minutos() {
        when(clienteUseCase.consultarPorTelefoneEUsuario(tel, TELEFONE_USUARIO)).thenReturn(Optional.of(cliente));
        when(conversaAgenteUseCase.consultarPorCliente(cliente.getId()))
                .thenReturn(conv);

        LocalDateTime fixed = LocalDateTime.of(2025,8,1,12,0);
        try (MockedStatic<LocalDateTime> mt = mockStatic(LocalDateTime.class)) {
            mt.when(LocalDateTime::now).thenReturn(fixed);
            when(conv.getFinalizada()).thenReturn(true);
            when(conv.getDataUltimaMensagem())
                    .thenReturn(fixed.minusMinutes(40));

            assertTrue(validadorTempoEspera.permitirProcessar(ctx));
            verifyNoInteractions(mensageriaUseCase);
        }
    }
}