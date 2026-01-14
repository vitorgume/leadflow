package com.gumeinteligencia.api_intermidiaria.application.gateways;

import com.gumeinteligencia.api_intermidiaria.domain.ConversaAgente;

import java.util.Optional;

public interface ConversaAgenteGateway {
    Optional<ConversaAgente> consultarPorTelefoneCliente(String telefone);
}
