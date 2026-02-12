package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.CrmUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.EscolhaVendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.*;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessarConversaInativaTest {

    @Mock
    private EscolhaVendedorUseCase escolhaVendedorUseCase;
    @Mock
    private CrmUseCase crmUseCase;
    @Mock
    private MensagemUseCase mensagemUseCase;
    @Mock
    private MensagemBuilder mensagemBuilder;

    @InjectMocks
    private ProcessarConversaInativa processador;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder().id(UUID.randomUUID()).build();
    }

    @Test
    void deveProcessarFluxoCompletoComSucesso() {
        ConversaAgente conversa = mock(ConversaAgente.class);
        Cliente cliente = mock(Cliente.class);
        when(cliente.getUsuario()).thenReturn(usuario);
        Vendedor vendedor = mock(Vendedor.class);

        when(cliente.getTelefone()).thenReturn("+5511999999999");
        when(vendedor.getNome()).thenReturn("Vendedor Teste");
        when(escolhaVendedorUseCase.escolherVendedor(cliente)).thenReturn(vendedor);
        when(mensagemBuilder.getMensagem(TipoMensagem.REDIRECIONAMENTO_RECONTATO, "Vendedor Teste", cliente))
                .thenReturn("mensagem-g1-direcionamento");

        processador.processar("resp", conversa, cliente);

        InOrder inOrder = inOrder(conversa, escolhaVendedorUseCase, mensagemBuilder, mensagemUseCase, crmUseCase);
        inOrder.verify(conversa).setFinalizada(true);
        inOrder.verify(escolhaVendedorUseCase).escolherVendedor(cliente);
        inOrder.verify(conversa).setVendedor(vendedor);
        inOrder.verify(mensagemBuilder).getMensagem(TipoMensagem.REDIRECIONAMENTO_RECONTATO, "Vendedor Teste", cliente);
        inOrder.verify(mensagemUseCase).enviarMensagem(eq("mensagem-g1-direcionamento"), eq("+5511999999999"), eq(true), any(Usuario.class));
        inOrder.verify(mensagemUseCase).enviarContato(vendedor, cliente);
        inOrder.verify(crmUseCase).atualizarCrm(vendedor, cliente, conversa);
    }

    @Test
    void devePropagarErroQuandoEnviarMensagemFalha() {
        ConversaAgente conversa = mock(ConversaAgente.class);
        Cliente cliente = mock(Cliente.class);
        when(cliente.getUsuario()).thenReturn(usuario);
        Vendedor vendedor = mock(Vendedor.class);

        when(cliente.getTelefone()).thenReturn("+5544999999999");
        when(vendedor.getNome()).thenReturn("Joao");
        when(escolhaVendedorUseCase.escolherVendedor(cliente)).thenReturn(vendedor);
        when(mensagemBuilder.getMensagem(TipoMensagem.REDIRECIONAMENTO_RECONTATO, "Joao", cliente)).thenReturn("msg");
        doThrow(new RuntimeException("erro-envio"))
                .when(mensagemUseCase).enviarMensagem(eq("msg"), eq("+5544999999999"), eq(true), any(Usuario.class));

        try {
            processador.processar("resp", conversa, cliente);
        } catch (RuntimeException ex) {
            // esperado propagar
        }

        verify(conversa).setFinalizada(true);
        verify(crmUseCase, never()).atualizarCrm(any(), any(), any());
    }

    @Test
    void deveContinuarMesmoSeEnviarContatoFalhar() {
        ConversaAgente conversa = mock(ConversaAgente.class);
        Cliente cliente = mock(Cliente.class);
        when(cliente.getUsuario()).thenReturn(usuario);
        Vendedor vendedor = mock(Vendedor.class);

        when(cliente.getTelefone()).thenReturn("+5577999999999");
        when(vendedor.getNome()).thenReturn("Ana");
        when(escolhaVendedorUseCase.escolherVendedor(cliente)).thenReturn(vendedor);
        when(mensagemBuilder.getMensagem(TipoMensagem.REDIRECIONAMENTO_RECONTATO, "Ana", cliente))
                .thenReturn("msg-ok");
        doThrow(new RuntimeException("erro-contato"))
                .when(mensagemUseCase).enviarContato(any(), any());

        processador.processar("resp", conversa, cliente);

        verify(mensagemUseCase).enviarMensagem(eq("msg-ok"), eq("+5577999999999"), eq(true), any(Usuario.class));
        verify(mensagemUseCase).enviarContato(vendedor, cliente);
        verify(crmUseCase).atualizarCrm(vendedor, cliente, conversa);
    }

    @Test
    void deveProcessar_quandoInativoCodigoZeroENaoFinalizado() {
        ConversaAgente conversa = mock(ConversaAgente.class);
        StatusConversa status = StatusConversa.INATIVO_G1;
        when(conversa.getStatus()).thenReturn(status);
        when(conversa.getFinalizada()).thenReturn(false);

        assertTrue(processador.deveProcessar("x", conversa));
    }

    @Test
    void naoDeveProcessar_quandoFinalizadaOuStatusDiferente() {
        ConversaAgente conversa = mock(ConversaAgente.class);
        when(conversa.getStatus()).thenReturn(StatusConversa.INATIVO_G2);
        when(conversa.getFinalizada()).thenReturn(false);
        assertFalse(processador.deveProcessar("x", conversa));

        when(conversa.getStatus()).thenReturn(StatusConversa.INATIVO_G1);
        when(conversa.getFinalizada()).thenReturn(true);
        assertFalse(processador.deveProcessar("x", conversa));
    }
}
