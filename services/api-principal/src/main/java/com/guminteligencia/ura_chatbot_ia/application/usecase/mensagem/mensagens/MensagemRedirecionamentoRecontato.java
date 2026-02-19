package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MensagemRedirecionamentoRecontato implements MensagemType {

    @Override
    public String getMensagem(String nomeVendedor, Cliente cliente) {
        String mensagem = cliente.getUsuario().getMensagemEncaminhamento();

        mensagem = mensagem.replaceAll("\\{nome_cliente\\}", cliente.getNome());
        mensagem = mensagem.replaceAll("\\{nome_vendedor\\}", nomeVendedor);

        for (Map.Entry<String, Object> dado : cliente.getAtributosQualificacao().entrySet()) {
            mensagem = mensagem.replaceAll("\\{" + dado.getKey() + "\\}", dado.getValue().toString());
        }

        return mensagem;
    }

    @Override
    public Integer getTipoMensagem() {
        return TipoMensagem.REDIRECIONAMENTO_RECONTATO.getCodigo();
    }
}
