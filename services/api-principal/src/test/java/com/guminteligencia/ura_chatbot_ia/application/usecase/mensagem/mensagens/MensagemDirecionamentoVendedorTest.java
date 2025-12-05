package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MensagemDirecionamentoVendedorTest {

    private final MensagemDirecionamentoVendedor mensagemDirecionamentoVendedor = new MensagemDirecionamentoVendedor();

    @Test
    void deveGerarMensagemComNomeEDor() {
        Cliente cliente = Cliente.builder()
                .nome("Joao")
                .telefone("+5511999887766")
                .dorDesejoPaciente("resultado")
                .build();

        String msg = mensagemDirecionamentoVendedor.getMensagem("ignored", cliente);

        assertTrue(msg.startsWith("Joao! Sua triagem"));
        assertTrue(msg.contains("resultado"));
    }

    @Test
    void deveGerarMensagemMesmoSemDor() {
        Cliente cliente = Cliente.builder()
                .nome("Maria")
                .telefone("+5511888777666")
                .build();

        String msg = mensagemDirecionamentoVendedor.getMensagem(null, cliente);
        assertTrue(msg.contains("Maria! Sua triagem"));
    }

    @Test
    void deveRetornarCodigoCorreto() {
        assertEquals(
                TipoMensagem.MENSAGEM_DIRECIONAMENTO_VENDEDOR.getCodigo(),
                mensagemDirecionamentoVendedor.getTipoMensagem()
        );
    }
}
