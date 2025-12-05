package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MensagemRecontatoIantivoG1Test {
    private final MensagemRecontatoIantivoG1 mensagem = new MensagemRecontatoIantivoG1();

    @Test
    void deveRetornarMensagemComDadosQuandoDisponiveis() {
        Cliente cliente = Cliente.builder()
                .nome("Fulano")
                .dorDesejoPaciente("procedimento X")
                .build();

        String resultado = mensagem.getMensagem(null, cliente);

        assertTrue(resultado.contains("Fulano"));
        assertTrue(resultado.contains("procedimento X"));
    }

    @Test
    void deveRetornarMensagemBasicaQuandoSemDados() {
        Cliente vazio = Cliente.builder().build();
        String resultado = mensagem.getMensagem(null, vazio);
        assertTrue(resultado.startsWith("Ol"));
    }

    @Test
    void deveRetornarCodigoDoTipoMensagemCorreto() {
        int codigo = mensagem.getTipoMensagem();
        assertEquals(TipoMensagem.RECONTATO_INATIVO_G1.getCodigo(), codigo);
    }

}
