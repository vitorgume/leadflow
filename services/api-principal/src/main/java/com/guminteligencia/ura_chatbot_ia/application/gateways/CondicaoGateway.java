package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Condicao;

import java.util.Optional;
import java.util.UUID;

public interface CondicaoGateway {
    Condicao salvar(Condicao condicao);

    Optional<Condicao> consultarPorId(UUID id);

    void deletar(UUID id);
}
