package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MensagemRecontatoTest {
    private final MensagemRecontato mensagemRecontato = new MensagemRecontato();

    @Test
    void deveRetornarMensagemPadrao() {
        Cliente cliente = Cliente.builder().nome("Carlos").telefone("+5511999000111").build();

        String msg = mensagemRecontato.getMensagem("Ana", cliente);

        assertEquals(
                "Identifiquei que você já estava em conversa com nossas atendentes, vou repassar você novamente.",
                msg
        );
    }

    @Test
    void deveRetornarCodigoCorreto() {
        int codigo = mensagemRecontato.getTipoMensagem();
        assertEquals(
                TipoMensagem.MENSAGEM_RECONTATO_VENDEDOR.getCodigo(),
                codigo
        );
    }

}
