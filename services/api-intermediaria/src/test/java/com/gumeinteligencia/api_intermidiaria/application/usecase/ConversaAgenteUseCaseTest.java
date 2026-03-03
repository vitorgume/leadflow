package com.gumeinteligencia.api_intermidiaria.application.usecase;

import com.gumeinteligencia.api_intermidiaria.application.gateways.ConversaAgenteGateway;
import com.gumeinteligencia.api_intermidiaria.domain.Cliente;
import com.gumeinteligencia.api_intermidiaria.domain.ConversaAgente;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversaAgenteUseCaseTest {

    @Mock
    private ConversaAgenteGateway gateway;

    @InjectMocks
    private ConversaAgenteUseCase useCase;

    private final UUID ID_CONVERSA = UUID.randomUUID();
    private final String TELEFONE_CLIENTE = "1144578456";

    @Test
    void deveConsultarConversaPeleTelefoneClienteComSucesso() {

        ConversaAgente existente = ConversaAgente.builder()
                .id(ID_CONVERSA)
                .cliente(Cliente.builder().id(UUID.randomUUID()).telefone(TELEFONE_CLIENTE).build())
                .dataCriacao(LocalDateTime.now())
                .finalizada(false)
                .dataUltimaMensagem(LocalDateTime.now())
                .recontato(false)
                .build();

        when(gateway.consultarPorTelefoneCliente(TELEFONE_CLIENTE)).thenReturn(Optional.of(existente));

        Optional<ConversaAgente> conversaAgente = useCase.consultarPorTelefoneCliente(TELEFONE_CLIENTE);

        Assertions.assertEquals(conversaAgente.get().getId(), ID_CONVERSA);
        verify(gateway).consultarPorTelefoneCliente(TELEFONE_CLIENTE);
    }

}