package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MensagemRecontatoIantivoG1 implements MensagemType {

    @Override
    public String getMensagem(String nomeVendedor, Cliente cliente) {

        String mensagem = cliente.getUsuario().getMensagemRecontatoG1();

        mensagem = mensagem.replaceAll("\\{nome_cliente\\}", cliente.getNome());

        for (Map.Entry<String, Object> dado : cliente.getAtributosQualificacao().entrySet()) {
            mensagem = mensagem.replaceAll("\\{" + dado.getKey() + "\\}", dado.getValue().toString());
        }

        return mensagem;
    }

    @Override
    public Integer getTipoMensagem() {
        return TipoMensagem.RECONTATO_INATIVO_G1.getCodigo();
    }
}
