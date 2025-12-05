package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.VendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.StatusConversa;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversaInativaUseCaseExtraTest {

    @Mock private ConversaAgenteUseCase conversaAgenteUseCase;
    @Mock private VendedorUseCase vendedorUseCase;
    @Mock private CrmUseCase crmUseCase;
    @Mock private MensagemUseCase mensagemUseCase;
    @Mock private MensagemBuilder mensagemBuilder;

    private ConversaInativaUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ConversaInativaUseCase(
                conversaAgenteUseCase, vendedorUseCase, crmUseCase, mensagemUseCase, mensagemBuilder, "dev"
        );
    }

    @Test
    void deveMarcarComoInativoG1EEnviarMensagemQuandoAindaEmAndamento() {
        Cliente cliente = Cliente.builder().id(UUID.randomUUID()).telefone("+5511").build();
        ConversaAgente conversa = ConversaAgente.builder()
                .cliente(cliente)
                .status(StatusConversa.ATIVO)
                .finalizada(false)
                .dataUltimaMensagem(LocalDateTime.now().minusSeconds(30))
                .build();

        when(conversaAgenteUseCase.listarNaoFinalizados()).thenReturn(List.of(conversa));
        when(mensagemBuilder.getMensagem(TipoMensagem.RECONTATO_INATIVO_G1, null, cliente))
                .thenReturn("ping");

        useCase.verificaAusenciaDeMensagem();

        assertEquals(StatusConversa.INATIVO_G1, conversa.getStatus());
        verify(mensagemUseCase).enviarMensagem("ping", cliente.getTelefone(), false);
        verify(conversaAgenteUseCase).salvar(conversa);
        verify(crmUseCase, never()).atualizarCrm(any(), any(), any());
    }

    @Test
    void deveMarcarComoInativoG2EChamarCrmQuandoFinalizada() {
        Cliente cliente = Cliente.builder().id(UUID.randomUUID()).telefone("+5522").build();
        ConversaAgente conversa = ConversaAgente.builder()
                .cliente(cliente)
                .status(StatusConversa.ATIVO)
                .finalizada(true)
                .dataUltimaMensagem(LocalDateTime.now().minusSeconds(30))
                .build();

        Vendedor vendedorPadrao = Vendedor.builder().id(10L).build();
        when(conversaAgenteUseCase.listarNaoFinalizados()).thenReturn(List.of(conversa));
        when(vendedorUseCase.consultarVendedorPadrao()).thenReturn(vendedorPadrao);

        useCase.verificaAusenciaDeMensagem();

        assertEquals(StatusConversa.INATIVO_G2, conversa.getStatus());
        assertEquals(vendedorPadrao, conversa.getVendedor());
        verify(crmUseCase).atualizarCrm(vendedorPadrao, cliente, conversa);
        verify(conversaAgenteUseCase).salvar(conversa);
        verify(mensagemUseCase, never()).enviarMensagem(any(), any(), anyBoolean());
    }
}
