package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CardDto;

import java.util.Optional;

public interface CrmGateway {
    Optional<Integer> consultaLeadPeloTelefone(String telefone);

    void atualizarCard(CardDto cardDto, Integer idLead);
}
