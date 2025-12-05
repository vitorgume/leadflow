package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.usecase.AgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ClienteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.CrmUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.VendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessarClienteQualificadoTest {

    @Mock
    private VendedorUseCase vendedorUseCase;
    @Mock
    private ClienteUseCase clienteUseCase;
    @Mock
    private MensagemUseCase mensagemUseCase;
    @Mock
    private MensagemBuilder mensagemBuilder;
    @Mock
    private AgenteUseCase agenteUseCase;
    @Mock
    private ConversaAgente conversaAgente;
    @Mock
    private Cliente originalCliente;
    @Mock
    private Cliente clienteSalvo;
    @Mock
    private Vendedor vendedor;
    @Mock
    private CrmUseCase crmUseCase;

    @InjectMocks
    private ProcessarClienteQualificado processarClienteQualificado;

    private final String resposta = "conteudo QUALIFICADO:true";
    private final UUID originalId = UUID.randomUUID();
    private final String telSalvo = "+5511999111222";

    @Test
    void deveProcessarExecutarFluxoCompleto() {
        Qualificacao qual = new Qualificacao();
        qual.setNome("Joao");
        qual.setTipoConsulta(0);
        qual.setPreferenciaHorario(0);
        when(agenteUseCase.enviarJsonTrasformacao(resposta)).thenReturn(qual);
        when(conversaAgente.getCliente()).thenReturn(originalCliente);
        when(originalCliente.getId()).thenReturn(originalId);

        ArgumentCaptor<Cliente> capCliente = ArgumentCaptor.forClass(Cliente.class);
        when(clienteUseCase.alterar(capCliente.capture(), eq(originalId)))
                .thenReturn(clienteSalvo);

        when(vendedorUseCase.consultarVendedorPadrao()).thenReturn(vendedor);
        when(vendedor.getNome()).thenReturn("Carlos");

        when(mensagemBuilder.getMensagem(
                TipoMensagem.MENSAGEM_DIRECIONAMENTO_VENDEDOR,
                "Carlos",
                clienteSalvo
        )).thenReturn("msg-dir");

        when(clienteSalvo.getTelefone()).thenReturn(telSalvo);

        processarClienteQualificado.processar(resposta, conversaAgente, originalCliente);

        Cliente built = capCliente.getValue();
        assertEquals("Joao", built.getNome());

        verify(vendedorUseCase).consultarVendedorPadrao();
        verify(mensagemUseCase).enviarMensagem("msg-dir", telSalvo, false);
        verify(mensagemUseCase).enviarContato(vendedor, clienteSalvo);
        verify(crmUseCase).atualizarCrm(vendedor, clienteSalvo, conversaAgente);
        verify(conversaAgente).setVendedor(vendedor);
        verify(conversaAgente).setStatus(StatusConversa.ATIVO);
        verify(conversaAgente).setFinalizada(true);
    }

    @Test
    void deveRetornarTrueSeRespostaContemQualificadoTrue() {
        assertTrue(processarClienteQualificado.deveProcessar(" QUALIFICADO:True  ", conversaAgente));
        assertTrue(processarClienteQualificado.deveProcessar("abcQualificado:truexyz", conversaAgente));
    }

    @Test
    void deveRetornaFalseParaNullSemFlagQualificadoFalse() {
        assertFalse(processarClienteQualificado.deveProcessar(null, conversaAgente));
        assertFalse(processarClienteQualificado.deveProcessar("qualificado:false", conversaAgente));
        assertFalse(processarClienteQualificado.deveProcessar("nada aqui", conversaAgente));
    }

    @Test
    void naoDevePropagarErroDoAgente() {
        when(agenteUseCase.enviarJsonTrasformacao(resposta))
                .thenThrow(new RuntimeException("boom"));

        assertThrows(RuntimeException.class,
                () -> processarClienteQualificado.processar(resposta, conversaAgente, originalCliente)
        );

        verifyNoInteractions(clienteUseCase, vendedorUseCase, mensagemUseCase, mensagemBuilder);
    }
}
