package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MensagemRedirecionamentoRecontatoTest {

    private final MensagemRedirecionamentoRecontato mensagem =
            new MensagemRedirecionamentoRecontato();

    @Test
    void deveRetornarMensagemPadrao() {
        Cliente cliente = Cliente.builder()
                .nome("Fulano")
                .telefone("+55 44 99999-9999")
                .build();

        String resultado = mensagem.getMensagem("Carlos", cliente);

        assertTrue(resultado.contains("redirecionando para nossas atendentes"));
    }

    @Test
    void deveRetornarCodigoDoTipoMensagemCorreto() {
        int codigo = mensagem.getTipoMensagem();
        assertEquals(TipoMensagem.REDIRECIONAMENTO_RECONTATO.getCodigo(), codigo);
    }
}
