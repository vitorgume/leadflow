package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem;

import com.guminteligencia.ura_chatbot_ia.application.gateways.MensagemGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.CriptografiaJCAUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MensagemUseCaseTest {

    @Mock
    private MensagemGateway gateway;

    @Mock
    private MensagemBuilder mensagemBuilder;

    @Mock
    private CriptografiaJCAUseCase criptografiaJCAUseCase;

    @InjectMocks
    private MensagemUseCase useCase;

    private final String texto = "texto qualquer";
    private final String telefone = "+5511999000111";

    private Vendedor vendedor;
    private Cliente cliente;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .whatsappIdInstance("instance")
                .whatsappToken("token")
                .enviarContato(true)
                .build();

        cliente = Cliente.builder()
                .id(java.util.UUID.randomUUID())
                .nome("ClienteX")
                .telefone(telefone)
                .usuario(usuario)
                .build();

        vendedor = Vendedor.builder()
                .id(1L)
                .nome("VendedorY")
                .telefone("+5511888777666")
                .build();
    }

    @Test
    void deveEnviarMensagemParaTelefoneComSucesso() {
        when(criptografiaJCAUseCase.descriptografar(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.enviarMensagem(texto, telefone, false, usuario);

        verify(gateway, times(1)).enviar(texto, telefone, "instance", "token");
    }

    @Test
    void deveLancarExceptionQuandoGatewayFalharEnviarMensgaem() {
        when(criptografiaJCAUseCase.descriptografar(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        doThrow(new IllegalStateException("erro-enviar")).when(gateway).enviar(texto, telefone, "instance", "token");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> useCase.enviarMensagem(texto, telefone, false, usuario)
        );
        assertEquals("erro-enviar", ex.getMessage());
    }

    @Test
    void deveEnviarContatoComSucesso() {
        String msgDados = "DADOS";
        String msgSep   = "----";
        when(mensagemBuilder.getMensagem(
                TipoMensagem.DADOS_CONTATO_VENDEDOR, null, cliente
        )).thenReturn(msgDados);
        when(mensagemBuilder.getMensagem(
                TipoMensagem.MENSAGEM_SEPARACAO, null, null
        )).thenReturn(msgSep);

        when(criptografiaJCAUseCase.descriptografar(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.enviarContato(vendedor, cliente);

        InOrder ord = inOrder(gateway);
        ord.verify(gateway).enviarContato(vendedor.getTelefone(), cliente, "instance", "token");
        ord.verify(gateway).enviar(msgDados, vendedor.getTelefone(), "instance", "token");
        ord.verify(gateway).enviar(msgSep, vendedor.getTelefone(), "instance", "token");
        ord.verifyNoMoreInteractions();
    }

    @Test
    void deveIgnorarExceptionQuandoEnviarContatoFalhar() {

        when(criptografiaJCAUseCase.descriptografar(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        doThrow(new RuntimeException("fail-contato"))
                .when(gateway).enviarContato(vendedor.getTelefone(), cliente, "instance", "token");

        assertDoesNotThrow(() -> useCase.enviarContato(vendedor, cliente));
        verify(gateway).enviarContato(vendedor.getTelefone(), cliente, "instance", "token");
    }

    @Test
    void naoDeveEnviarContatoCasoConfiguracaoUsuarioEstejaParaNaoEnviar() {
        cliente.getUsuario().setEnviarContato(false);

        String msgDados = "DADOS";
        String msgSep   = "----";

        useCase.enviarContato(vendedor, cliente);

        Mockito.verify(gateway, never()).enviarContato(vendedor.getTelefone(), cliente, "instance", "token");
        Mockito.verify(gateway, never()).enviar(msgDados, vendedor.getTelefone(), "instance", "token");
        Mockito.verify(gateway, never()).enviar(msgSep, vendedor.getTelefone(), "instance", "token");
    }


    @Test
    void deveEnviarRelatorioComSucesso() {
        String arquivo = "base64xxx";
        String nomeArquivo = "rel.xlsx";

        when(criptografiaJCAUseCase.descriptografar(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.enviarRelatorio(arquivo, nomeArquivo, telefone, usuario);

        verify(gateway, times(1))
                .enviarRelatorio(arquivo, nomeArquivo, telefone, "instance", "token");
    }

    @Test
    void deveLancarExceptionQuandoEnvioDeRelatorioFalhar() {
        doThrow(new IllegalArgumentException("erro-rel"))
                .when(gateway).enviarRelatorio(anyString(), anyString(), anyString(), anyString(), anyString());

        when(criptografiaJCAUseCase.descriptografar(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.enviarRelatorio("a", "b", "c", usuario)
        );
        assertEquals("erro-rel", ex.getMessage());

        verify(gateway).enviarRelatorio("a", "b", "c", "instance", "token");
    }
}
