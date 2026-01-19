package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class MensagemDadosContatoAtendente implements MensagemType {

    @Override
    public String getMensagem(String nomeVendedor, Cliente cliente) {
        StringBuilder mensagem = new StringBuilder();
        LocalDateTime dataHoje = LocalDateTime.now();

        String horaMinutos = String.format("%02d:%02d", dataHoje.getHour(), dataHoje.getMinute());

        mensagem.append("Dados do contato acima:\n");

        if(cliente.getNome() != null) {
            mensagem.append("Nome: ").append(cliente.getNome()).append("\n");
        } else {
            mensagem.append("Nome: ").append("Nome não informado").append("\n");
        }

        for (Map.Entry<String, Object> dado : cliente.getAtributosQualificacao().entrySet()) {
            String nomeCampo = dado.getKey();
            Object valorCampo = dado.getValue();

            nomeCampo = nomeCampo.replace("_", " ");
            String nomeCampoNormalizado = nomeCampo.substring(0, 1).toUpperCase() + nomeCampo.substring(1);

            if(valorCampo != null) {
                mensagem.append(nomeCampoNormalizado + ": ").append(valorCampo).append("\n");
            } else {
                mensagem.append(nomeCampoNormalizado + ": ").append(nomeCampoNormalizado + " não informado");
            }
        }

        if(cliente.getTelefone() != null) {
            mensagem.append("Telefone: ").append(cliente.getTelefone()).append("\n");
        } else {
            mensagem.append("Telefone: ").append("Telefone não informado").append("\n");
        }

        mensagem.append("Hora: ").append(horaMinutos).append("\n");

        return mensagem.toString();
    }

    @Override
    public Integer getTipoMensagem() {
        return TipoMensagem.DADOS_CONTATO_VENDEDOR.getCodigo();
    }
}
