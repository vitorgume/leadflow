package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.ConversaAgenteNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ClienteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ConversaAgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.MensageriaUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.ContextoUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.ProcessamentoContextoNovoUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente.ProcessamentoContextoExistente;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.validador.ContextoValidadorComposite;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessamentoMensagemUseCaseUnitTest {

    @Mock private MensageriaUseCase mensageriaUseCase;
    @Mock private ContextoUseCase contextoUseCase;
    @Mock private ClienteUseCase clienteUseCase;
    @Mock private ContextoValidadorComposite contextoValidadorComposite;
    @Mock private ProcessamentoContextoExistente processamentoContextoExistente;
    @Mock private ProcessamentoContextoNovoUseCase processamentoContextoNovoUseCase;
    @Mock private ConversaAgenteUseCase conversaAgenteUseCase;

    @InjectMocks
    private ProcessamentoMensagemUseCase useCase;

    private Method processarMensagemMethod;

//    @BeforeEach
//    void setup() throws Exception {
//        processarMensagemMethod = ProcessamentoMensagemUseCase.class
//                .getDeclaredMethod("processarMensagem", Contexto.class);
//        processarMensagemMethod.setAccessible(true);
//    }
//
//    @Test
//    void deveProcessarContextoNovoQuandoClienteNaoPossuiConversa() throws Exception {
//        Contexto contexto = Contexto.builder().telefone("+5511").build();
//        Cliente cliente = Cliente.builder().telefone("+5511").build();
//
//        when(clienteUseCase.consultarPorTelefone("+5511"))
//                .thenReturn(Optional.of(cliente));
//        when(conversaAgenteUseCase.consultarPorCliente(any()))
//                .thenThrow(new ConversaAgenteNaoEncontradoException());
//
//        processarMensagemMethod.invoke(useCase, contexto);
//
//        verify(processamentoContextoNovoUseCase).processarContextoNovo(contexto);
//        verify(processamentoContextoExistente, never()).processarContextoExistente(any(), any());
//    }
//
//    @Test
//    void deveProcessarContextoExistenteQuandoConversaEncontrada() throws Exception {
//        Contexto contexto = Contexto.builder().telefone("+5522").build();
//        Cliente cliente = Cliente.builder().telefone("+5522").build();
//        ConversaAgente conversa = ConversaAgente.builder().id(UUID.randomUUID()).build();
//
//        when(clienteUseCase.consultarPorTelefone("+5522"))
//                .thenReturn(Optional.of(cliente));
//        when(conversaAgenteUseCase.consultarPorCliente(any()))
//                .thenReturn(conversa);
//
//        processarMensagemMethod.invoke(useCase, contexto);
//
//        verify(processamentoContextoExistente).processarContextoExistente(cliente, contexto);
//        verify(processamentoContextoNovoUseCase, never()).processarContextoNovo(any());
//    }
//
//    @Test
//    void deveProcessarContextoNovoQuandoClienteNaoEncontrado() throws Exception {
//        Contexto contexto = Contexto.builder().telefone("+5533").build();
//        when(clienteUseCase.consultarPorTelefone("+5533"))
//                .thenReturn(Optional.empty());
//
//        processarMensagemMethod.invoke(useCase, contexto);
//
//        verify(processamentoContextoNovoUseCase).processarContextoNovo(contexto);
//        verify(processamentoContextoExistente, never()).processarContextoExistente(any(), any());
//    }
}
