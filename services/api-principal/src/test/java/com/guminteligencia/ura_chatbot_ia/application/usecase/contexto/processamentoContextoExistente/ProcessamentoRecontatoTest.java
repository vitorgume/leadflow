package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.usecase.ConversaAgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.OutroContatoUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessamentoRecontatoTest {

    @Mock
    private MensagemUseCase mensagemUseCase;
    @Mock
    private MensagemBuilder mensagemBuilder;
    @Mock
    private OutroContatoUseCase outroContatoUseCase;
    @Mock
    private ConversaAgenteUseCase conversaAgenteUseCase;

    @InjectMocks
    private ProcessamentoRecontato processamento;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder().id(UUID.randomUUID()).build();
    }

    @Test
    void deveEnviarContatoEAjustarConversaQuandoNaoFoiRecontato() {
        Vendedor vendedor = Vendedor.builder().id(1L).nome("Vendedor").telefone("+5511").build();
        Cliente cliente = Cliente.builder().id(UUID.randomUUID()).telefone("+5522").usuario(usuario).build();
        ConversaAgente conversa = ConversaAgente.builder()
                .cliente(cliente)
                .vendedor(vendedor)
                .recontato(false)
                .finalizada(true)
                .build();

        when(mensagemBuilder.getMensagem(TipoMensagem.MENSAGEM_RECONTATO_VENDEDOR, vendedor.getNome(), null))
                .thenReturn("mensagem-recontato");

        processamento.processar("resposta", conversa, cliente);

        verify(mensagemUseCase).enviarMensagem(eq("mensagem-recontato"), eq(cliente.getTelefone()), eq(false), any(Usuario.class));
        verify(mensagemUseCase).enviarContato(vendedor, cliente);
        verify(conversaAgenteUseCase).salvar(conversa);
        assertTrue(conversa.getRecontato());
    }

    @Test
    void deveResponderDiretoQuandoJaFoiRecontato() {
        Cliente cliente = Cliente.builder().telefone("+5533").id(UUID.randomUUID()).usuario(usuario).build();
        ConversaAgente conversa = ConversaAgente.builder()
                .cliente(cliente)
                .recontato(true)
                .finalizada(true)
                .build();

        processamento.processar("texto usuario", conversa, cliente);

        verify(mensagemUseCase).enviarMensagem(eq("texto usuario"), eq(cliente.getTelefone()), eq(true), any(Usuario.class));
        verify(conversaAgenteUseCase, never()).salvar(any());
        verify(mensagemBuilder, never()).getMensagem(any(), any(), any());
    }

    @Test
    void deveProcessarSomenteQuandoConversaFinalizada() {
        ConversaAgente conversaFinalizada = ConversaAgente.builder().finalizada(true).build();
        ConversaAgente conversaAberta = ConversaAgente.builder().finalizada(false).build();

        assertTrue(processamento.deveProcessar("resp", conversaFinalizada));
        assertTrue(!processamento.deveProcessar("resp", conversaAberta));
    }
}
