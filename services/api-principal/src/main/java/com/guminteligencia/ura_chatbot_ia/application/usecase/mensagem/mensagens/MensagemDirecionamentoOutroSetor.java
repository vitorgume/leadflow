package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MensagemDirecionamentoOutroSetor implements MensagemType {

    @Override
    public String getMensagem(String nomeMemebro, Cliente cliente) {
        String mensagem = cliente.getUsuario().getMensagemDirecionamentoVendedor();

        mensagem = mensagem.replaceAll("\\{nome_cliente\\}", cliente.getNome());
        mensagem = mensagem.replaceAll("\\{nome_membro\\}", nomeMemebro);

        for (Map.Entry<String, Object> dado : cliente.getAtributosQualificacao().entrySet()) {
            mensagem = mensagem.replaceAll("\\{" + dado.getKey() + "\\}", dado.getValue().toString());
        }

        return mensagem;
    }

    @Override
    public Integer getTipoMensagem() {
        return TipoMensagem.MENSAGEM_DIRECIONAMENTO_OUTRO_SETOR.getCodigo();
    }
}
