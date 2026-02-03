package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.usecase.AgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ClienteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.CrmUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.EscolhaVendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.VendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.*;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
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
    private EscolhaVendedorUseCase escolhaVendedorUseCase;

    @Mock
    private Vendedor vendedor;
    @Mock
    private CrmUseCase crmUseCase;

    @InjectMocks
    private ProcessarClienteQualificado processarClienteQualificado;

    private final String resposta = "conteudo QUALIFICADO:true";
    private final UUID ID_USUARIO = UUID.randomUUID();

    private final Usuario usuarioDomain = Usuario.builder()
            .id(ID_USUARIO)
            .nome("nome teste")
            .telefone("00000000000")
            .senha("senhateste123")
            .email("emailteste@123")
            .telefoneConcectado("00000000000")
            .atributosQualificacao(Map.of("teste", new Object()))
            .configuracaoCrm(
                    ConfiguracaoCrm.builder()
                            .crmType(CrmType.KOMMO)
                            .mapeamentoCampos(Map.of("teste", "teste"))
                            .idTagAtivo("id-teste")
                            .idTagAtivo("id-teste")
                            .idEtapaAtivos("id-teste")
                            .idEtapaInativos("id-teste")
                            .acessToken("acess-token-teste")
                            .build()
            )
            .mensagemDirecionamentoVendedor("mensagem-teste")
            .mensagemRecontatoG1("mensagem-teste")
            .whatsappToken("token-teste")
            .whatsappIdInstance("id-teste")
            .agenteApiKey("api-key-teste")
            .build();

    private final Cliente cliente = Cliente.builder()
            .id(UUID.randomUUID())
            .nome("Nome domain")
            .telefone("00000000000")
            .atributosQualificacao(Map.of("teste", new Object()))
            .inativo(false)
            .usuario(usuarioDomain)
            .build();

    @Test
    void deveProcessarExecutarFluxoCompleto() {
        Qualificacao qual = new Qualificacao();
        qual.setNome("Joao");
        when(agenteUseCase.enviarJsonTrasformacao(resposta, ID_USUARIO)).thenReturn(qual);
        when(conversaAgente.getCliente()).thenReturn(cliente);

        ArgumentCaptor<Cliente> capCliente = ArgumentCaptor.forClass(Cliente.class);
        when(clienteUseCase.alterar(capCliente.capture(), Mockito.any()))
                .thenReturn(cliente);

        when(vendedor.getNome()).thenReturn("Carlos");

        when(mensagemBuilder.getMensagem(
                TipoMensagem.MENSAGEM_DIRECIONAMENTO_VENDEDOR,
                "Carlos",
                cliente
        )).thenReturn("msg-dir");

        when(escolhaVendedorUseCase.escolherVendedor(cliente)).thenReturn(vendedor);

        processarClienteQualificado.processar(resposta, conversaAgente, cliente);

        Cliente built = capCliente.getValue();
        assertEquals("Joao", built.getNome());

        verify(escolhaVendedorUseCase).escolherVendedor(cliente);
        verify(mensagemUseCase).enviarMensagem("msg-dir", cliente.getTelefone(), false);
        verify(mensagemUseCase).enviarContato(vendedor, cliente);
        verify(crmUseCase).atualizarCrm(vendedor, cliente, conversaAgente);
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
        when(agenteUseCase.enviarJsonTrasformacao(resposta, ID_USUARIO))
                .thenThrow(new RuntimeException("boom"));

        assertThrows(RuntimeException.class,
                () -> processarClienteQualificado.processar(resposta, conversaAgente, cliente)
        );

        verifyNoInteractions(clienteUseCase, vendedorUseCase, mensagemUseCase, mensagemBuilder);
    }
}
