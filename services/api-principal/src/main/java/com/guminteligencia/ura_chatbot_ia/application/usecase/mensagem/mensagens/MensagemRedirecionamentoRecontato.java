package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.springframework.stereotype.Component;

@Component
public class MensagemRedirecionamentoRecontato implements MensagemType {

    @Override
    public String getMensagem(String nomeVendedor, Cliente cliente) {
        return "Perfeito, estou te redirecionando para nossas atendentes que logo entrará em contato. Muito obrigado ! Até...";
    }

    @Override
    public Integer getTipoMensagem() {
        return TipoMensagem.REDIRECIONAMENTO_RECONTATO.getCodigo();
    }
}
