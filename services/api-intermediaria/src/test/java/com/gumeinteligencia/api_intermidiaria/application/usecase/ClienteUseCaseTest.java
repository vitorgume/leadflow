package com.gumeinteligencia.api_intermidiaria.application.usecase;

import com.gumeinteligencia.api_intermidiaria.application.gateways.ClienteGateway;
import com.gumeinteligencia.api_intermidiaria.domain.Cliente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteUseCaseTest {

    @Mock
    private ClienteGateway clienteGateway;

    @InjectMocks
    private ClienteUseCase clienteUseCase;

    @Test
    void deveConsultarClientePorTelefoneQuandoExistir() {
        Cliente cliente = Cliente.builder()
                .id(UUID.randomUUID())
                .nome("Fulano")
                .telefone("44999999999")
                .build();

        when(clienteGateway.consultarPorTelefone(cliente.getTelefone()))
                .thenReturn(Optional.of(cliente));

        Optional<Cliente> resultado = clienteUseCase.consultarPorTelefone(cliente.getTelefone());

        assertTrue(resultado.isPresent());
        assertEquals(cliente.getTelefone(), resultado.get().getTelefone());
    }

    @Test
    void deveRetornarVazioQuandoClienteNaoExistir() {
        when(clienteGateway.consultarPorTelefone("000")).thenReturn(Optional.empty());

        Optional<Cliente> resultado = clienteUseCase.consultarPorTelefone("000");

        assertTrue(resultado.isEmpty());
    }

}
