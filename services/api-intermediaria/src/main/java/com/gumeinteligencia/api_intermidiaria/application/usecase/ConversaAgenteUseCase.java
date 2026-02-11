package com.gumeinteligencia.api_intermidiaria.application.usecase;

import com.gumeinteligencia.api_intermidiaria.application.exceptions.ConversaAgenteNaoEncontradoException;
import com.gumeinteligencia.api_intermidiaria.application.gateways.ConversaAgenteGateway;
import com.gumeinteligencia.api_intermidiaria.domain.ConversaAgente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConversaAgenteUseCase {

    private final ConversaAgenteGateway gateway;

    public Optional<ConversaAgente> consultarPorTelefoneCliente(String telefone) {
        return gateway.consultarPorTelefoneCliente(telefone);
    }
}
