package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.MensagemAgenteDto;
import com.guminteligencia.ura_chatbot_ia.domain.Qualificacao;
import com.guminteligencia.ura_chatbot_ia.domain.RespostaAgente;

import java.util.UUID;

public interface AgenteGateway {
    String enviarMensagem(MensagemAgenteDto mensagem);

    String enviarJsonTrasformacao(String texto, UUID idUsuario);
}
