package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.EscolhaVendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.StatusConversa;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessarEncaminhamentoAtendenteTest {

    @Mock
    private MensagemUseCase mensagemUseCase;
    @Mock
    private MensagemBuilder mensagemBuilder;
    @Mock
    private EscolhaVendedorUseCase escolhaVendedorUseCase;

    @InjectMocks
    private ProcessarEncaminhamentoAtendente processador;

    @Test
    void deveEncaminharParaAtendenteEAjustarConversa() {
        Vendedor vendedor = Vendedor.builder().id(10L).telefone("+5500").build();
        Cliente cliente = Cliente.builder().id(UUID.randomUUID()).telefone("+5511").usuario(Usuario.builder().id(UUID.randomUUID()).build()).build();
        ConversaAgente conversa = ConversaAgente.builder()
                .cliente(cliente)
                .finalizada(false)
                .status(StatusConversa.ANDAMENTO)
                .build();

        when(escolhaVendedorUseCase.escolherVendedor(cliente)).thenReturn(vendedor);
        when(mensagemBuilder.getMensagem(TipoMensagem.REDIRECIONAMENTO_RECONTATO, null, null))
                .thenReturn("msg");

        processador.processar("encaminhar:true", conversa, cliente);

        verify(mensagemUseCase).enviarContato(vendedor, conversa.getCliente());
        verify(mensagemUseCase).enviarMensagem("msg", conversa.getCliente().getTelefone(), false);
        assertTrue(conversa.getFinalizada());
        assertTrue(conversa.getVendedor() == vendedor);
        assertTrue(conversa.getStatus() == StatusConversa.ATIVO);
    }

    @Test
    void deveProcessarApenasQuandoTextoContemEncaminharTrue() {
        ConversaAgente conversa = ConversaAgente.builder().build();
        assertTrue(processador.deveProcessar("  encaminhar:true  ", conversa));
        assertFalse(processador.deveProcessar("encaminhar:false", conversa));
        assertFalse(processador.deveProcessar(null, conversa));
    }
}
