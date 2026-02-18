package com.guminteligencia.ura_chatbot_ia.application.gateways.crm;

import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.moskit.ContatoMoskitDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.moskit.PayloadMoskit;

public interface IntegracaoMoskitGateway {
    void criarNegocio(PayloadMoskit payloadMoskit, String acessToken, String crmUrl);

    Integer criarContato(ContatoMoskitDto contatoMoskitDto, String acessToken, String crmUrl);
}
