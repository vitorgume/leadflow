package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem;

import com.guminteligencia.ura_chatbot_ia.application.gateways.MensagemGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MensagemUseCaseExtendedTest {

    @Mock
    private MensagemGateway gateway;
    @Mock
    private MensagemBuilder mensagemBuilder;

    @InjectMocks
    private MensagemUseCase useCase;

    @Test
    void deveNormalizarMensagemRemovendoQuebrasEEspaços() {
        String mensagemComQuebras = "\"Oi\\n\\r\\nTudo bem?\"";
        useCase.enviarMensagem(mensagemComQuebras, "+5511", true);

        verify(gateway).enviar(eq("Oi Tudo bem?"), eq("+5511"));
    }

    @Test
    void deveEnviarContatoParaVendedorMesmoQuandoGatewayFalhar() {
        Vendedor vendedor = Vendedor.builder().telefone("+5522").build();
        Cliente cliente = Cliente.builder().telefone("+5533").build();

        when(mensagemBuilder.getMensagem(TipoMensagem.DADOS_CONTATO_VENDEDOR, null, cliente))
                .thenReturn("dados");
        when(mensagemBuilder.getMensagem(TipoMensagem.MENSAGEM_SEPARACAO, null, null))
                .thenReturn("sep");

        doThrow(new RuntimeException("erro enviar contato"))
                .when(gateway).enviarContato(vendedor.getTelefone(), cliente);

        useCase.enviarContato(vendedor, cliente);

        verify(gateway).enviarContato(vendedor.getTelefone(), cliente);
        verify(gateway).enviar("dados", vendedor.getTelefone());
        verify(gateway).enviar("sep", vendedor.getTelefone());
    }
}
