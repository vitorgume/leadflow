package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.CrmUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.CrmIntegracaoFactory;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.CrmIntegracaoType;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import com.guminteligencia.ura_chatbot_ia.domain.StatusConversa;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrmUseCaseTest {

    @Mock
    private CrmIntegracaoFactory crmIntegracaoFactory;

    @InjectMocks
    private CrmUseCase useCase;

    @Mock
    private Vendedor vendedor;

    @Mock
    private Cliente cliente;

    @Mock
    private ConversaAgente conversaAgente;
    @Mock
    private CrmIntegracaoType crmIntegracaoType;

    @BeforeEach
    void setUp() {
        useCase = new CrmUseCase(
                crmIntegracaoFactory
        );

        conversaAgente = ConversaAgente.builder()
                .id(UUID.randomUUID())
                .status(StatusConversa.INATIVO_G1)
                .build();
    }
//

//
//    // --------------- atualizarCrm ----------------
//
    @Test
    void atualizarCrm_deveAtualizarSemMidia_eClienteAtivo() {
        when(cliente.getUsuario()).thenReturn(Usuario.builder().configuracaoCrm(ConfiguracaoCrm.builder().crmType(CrmType.KOMMO).build()).build());
        when(crmIntegracaoFactory.create(any(CrmType.class))).thenReturn(crmIntegracaoType);

        // act
        useCase.atualizarCrm(vendedor, cliente, conversaAgente);

        verify(crmIntegracaoType).implementacao(vendedor, cliente, conversaAgente);
    }


    @Test
    void atualizarCrm_devePropagarExcecaoDeAtualizarCard() {
        when(cliente.getUsuario()).thenReturn(Usuario.builder().configuracaoCrm(ConfiguracaoCrm.builder().crmType(CrmType.KOMMO).build()).build());
        when(crmIntegracaoFactory.create(any(CrmType.class))).thenReturn(crmIntegracaoType);
        doThrow(new DataProviderException("patch-fail", null))
                .when(crmIntegracaoType).implementacao(any(Vendedor.class), any(Cliente.class), any(ConversaAgente.class));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> useCase.atualizarCrm(vendedor, cliente, conversaAgente)
        );
        assertEquals("patch-fail", ex.getMessage());
    }



}
