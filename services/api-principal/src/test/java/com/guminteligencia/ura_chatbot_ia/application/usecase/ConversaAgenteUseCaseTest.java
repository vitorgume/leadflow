package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.ConversaAgenteNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.ConversaAgenteGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversaAgenteUseCaseTest {

    @Mock
    private ConversaAgenteGateway gateway;

    @InjectMocks
    private ConversaAgenteUseCase useCase;

    private Cliente cliente;
    private UUID clienteId;

    @BeforeEach
    void setup() {
        clienteId = UUID.randomUUID();
        cliente = Cliente.builder()
                .id(clienteId)
                .telefone("+5511999000111")
                .build();
    }

    @Test
    void deveCriarNovaConversaComCamposIniciaisEDelegarAoGateway() {
        LocalDateTime fixedNow = LocalDateTime.of(2025, 8, 4, 15, 30);
        ConversaAgente saved = ConversaAgente.builder()
                .id(UUID.randomUUID())
                .cliente(cliente)
                .build();

        // --- CORREÇÃO AQUI: Adicione Mockito.CALLS_REAL_METHODS ---
        try (MockedStatic<LocalDateTime> mt = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            mt.when(LocalDateTime::now).thenReturn(fixedNow);

            when(gateway.salvar(any())).thenReturn(saved);

            ConversaAgente result = useCase.criar(cliente);

            ArgumentCaptor<ConversaAgente> cap = ArgumentCaptor.forClass(ConversaAgente.class);
            verify(gateway).salvar(cap.capture());
            ConversaAgente toSave = cap.getValue();

            assertEquals(cliente, toSave.getCliente());
            assertEquals(fixedNow, toSave.getDataCriacao());
            assertFalse(toSave.getFinalizada());
            assertFalse(toSave.getRecontato());

            assertSame(saved, result);
        }
    }

    @Test
    void deveConsultarPorClienteQuandoEncontrar() {
        ConversaAgente conv = ConversaAgente.builder()
                .id(UUID.randomUUID())
                .cliente(cliente)
                .build();
        when(gateway.consultarPorIdCliente(clienteId)).thenReturn(Optional.of(conv));

        ConversaAgente result = useCase.consultarPorCliente(clienteId);
        assertSame(conv, result);
        verify(gateway).consultarPorIdCliente(clienteId);
    }

    @Test
    void deveLancarExcecaoQuandoConsultarPorClienteNaoEncontrado() {
        when(gateway.consultarPorIdCliente(clienteId)).thenReturn(Optional.empty());

        assertThrows(
                ConversaAgenteNaoEncontradoException.class,
                () -> useCase.consultarPorCliente(clienteId),
                "Se não existir conversa para o cliente, deve lançar ConversaAgenteNaoEncontradoException"
        );
        verify(gateway).consultarPorIdCliente(clienteId);
    }

    @Test
    void deveSalvarConversaDelegandoAoGateway() {
        ConversaAgente conv = mock(ConversaAgente.class);
        useCase.salvar(conv);
        verify(gateway).salvar(conv);
    }

    @Test
    void deveListarNaoFinalizadosDelegandoAoGateway() {
        List<ConversaAgente> lista = List.of(
                ConversaAgente.builder().id(UUID.randomUUID()).build(),
                ConversaAgente.builder().id(UUID.randomUUID()).build()
        );
        when(gateway.listarNaoFinalizados()).thenReturn(lista);

        List<ConversaAgente> result = useCase.listarNaoFinalizados();
        assertSame(lista, result);
        verify(gateway).listarNaoFinalizados();
    }

    @Test
    void deveConsultarPorIdQuandoEncontrar() {
        UUID conversaId = UUID.randomUUID();
        ConversaAgente conv = ConversaAgente.builder()
                .id(conversaId)
                .cliente(cliente)
                .build();
        when(gateway.consultarPorId(conversaId)).thenReturn(Optional.of(conv));

        ConversaAgente result = useCase.consultarPorId(conversaId);
        assertSame(conv, result);
        verify(gateway).consultarPorId(conversaId);
    }

    @Test
    void deveLancarExcecaoQuandoConsultarPorIdNaoEncontrado() {
        UUID conversaId = UUID.randomUUID();
        when(gateway.consultarPorId(conversaId)).thenReturn(Optional.empty());

        assertThrows(
                ConversaAgenteNaoEncontradoException.class,
                () -> useCase.consultarPorId(conversaId),
                "Se não existir conversa para o id, deve lançar ConversaAgenteNaoEncontradoException"
        );
        verify(gateway).consultarPorId(conversaId);
    }
}