package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.kommo.PayloadKommo;

import java.util.Optional;

public interface IntegracaoKommoGateway {
    Optional<Integer> consultaLeadPeloTelefone(String telefone, String acessToken, String crmUrl);

    void atualizarCard(PayloadKommo payloadKommo, Integer idLead, String acessToken, String crmUrl);
}
