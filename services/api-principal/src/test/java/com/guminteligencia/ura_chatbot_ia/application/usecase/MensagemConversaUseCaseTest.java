package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.gateways.MensagemConversaGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.MensagemConversa;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class MensagemConversaUseCaseTest {

    @Mock
    private MensagemConversaGateway gateway;

    @InjectMocks
    private MensagemConversaUseCase conversaUseCase;

    private List<MensagemConversa> mensagensConversa;

    @BeforeEach
    void setUp() {
        mensagensConversa = List.of(
                MensagemConversa.builder()
                        .id(UUID.randomUUID())
                        .responsavel("agente")
                        .data(LocalDateTime.of(2025, Month.OCTOBER, 8, 14, 41))
                        .conversaAgente(ConversaAgente.builder()
                                .id(UUID.randomUUID())
                                .cliente(Cliente.builder().id(UUID.randomUUID()).build())
                                .vendedor(Vendedor.builder().id(1L).build())
                                .build()
                        )
                        .build(),
                MensagemConversa.builder()
                        .id(UUID.randomUUID())
                        .responsavel("agente")
                        .data(LocalDateTime.of(2025, Month.OCTOBER, 8, 14, 41))
                        .conversaAgente(ConversaAgente.builder()
                                .id(UUID.randomUUID())
                                .cliente(Cliente.builder().id(UUID.randomUUID()).build())
                                .vendedor(Vendedor.builder().id(1L).build())
                                .build()
                        )
                        .build()
        );
    }

    @Test
    void deveListarMensagensPelaConversaComSucesso() {
        Mockito.when(gateway.listarPelaConversa(Mockito.any())).thenReturn(mensagensConversa);

        List<MensagemConversa> mensagens = conversaUseCase.listarPelaConversa(UUID.randomUUID());

        Mockito.verify(gateway).listarPelaConversa(Mockito.any());

        Assertions.assertEquals(mensagensConversa.get(0).getId(), mensagens.get(0).getId());
        Assertions.assertEquals(mensagensConversa.get(1).getId(), mensagens.get(1).getId());
    }
}