package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.LeadNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.CrmGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CardDto;
import com.guminteligencia.ura_chatbot_ia.domain.*;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrmUseCaseTest {

    @Mock
    private CrmGateway gateway;

    @InjectMocks
    private CrmUseCase useCase;

    @Mock
    private Vendedor vendedor;

    @Mock
    private Cliente cliente;

    @Mock
    private ConversaAgente conversaAgente;

    @BeforeEach
    void setUp() {
        useCase = new CrmUseCase(
                gateway,
                "prod"
        );

        conversaAgente = ConversaAgente.builder()
                .id(UUID.randomUUID())
                .status(StatusConversa.INATIVO_G1)
                .build();
    }

    private final String tel = "+5511999999999";
    private final Integer idLead = 12345;
    private final String urlChat = "https://chat.example/sala/abc";

    // --------------- atualizarCrm ----------------

    @Test
    void atualizarCrm_deveAtualizarSemMidia_eClienteAtivo() {
        // dados base
        String tel = "+5511999999999";
        int idLead = 42;

        when(cliente.getTelefone()).thenReturn(tel);
        when(vendedor.getIdVendedorCrm()).thenReturn(999);

        // lead encontrado
        when(gateway.consultaLeadPeloTelefone(tel)).thenReturn(Optional.of(idLead));

        // patch OK
        doNothing().when(gateway).atualizarCard(any(CardDto.class), eq(idLead));

        // act
        useCase.atualizarCrm(vendedor, cliente, conversaAgente);

        ArgumentCaptor<CardDto> captor = ArgumentCaptor.forClass(CardDto.class);
        verify(gateway).atualizarCard(captor.capture(), eq(idLead));
        assertNotNull(captor.getValue());
    }


    @Test
    void atualizarCrm_devePropagarExcecaoDeAtualizarCard() {

        // dados base
        String tel = "+5511999999999";
        int idLead = 42;

        when(cliente.getTelefone()).thenReturn(tel);

        when(vendedor.getIdVendedorCrm()).thenReturn(999);

        // lead encontrado
        when(gateway.consultaLeadPeloTelefone(tel)).thenReturn(Optional.of(idLead));

        doThrow(new DataProviderException("patch-fail", null))
                .when(gateway).atualizarCard(any(CardDto.class), eq(idLead));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> useCase.atualizarCrm(vendedor, cliente, conversaAgente)
        );
        assertEquals("patch-fail", ex.getMessage());
    }

    // --------------- consultaLeadPeloTelefone ----------------

    @Test
    void consultaLeadPeloTelefone_deveRetornarId() {
        String tel = "+5511999999999";

        int idLead = 42;

        // lead encontrado
        when(gateway.consultaLeadPeloTelefone(tel)).thenReturn(Optional.of(idLead));

        Integer out = useCase.consultaLeadPeloTelefone(tel);
        assertEquals(idLead, out);
        verify(gateway).consultaLeadPeloTelefone(tel);
    }

    @Test
    void consultaLeadPeloTelefone_deveLancarLeadNaoEncontradoQuandoEmpty() {
        when(gateway.consultaLeadPeloTelefone(tel)).thenReturn(Optional.empty());
        assertThrows(
                LeadNaoEncontradoException.class,
                () -> useCase.consultaLeadPeloTelefone(tel)
        );
    }

}
