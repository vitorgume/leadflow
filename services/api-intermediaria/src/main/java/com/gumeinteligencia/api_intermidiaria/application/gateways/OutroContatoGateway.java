package com.gumeinteligencia.api_intermidiaria.application.gateways;

import com.gumeinteligencia.api_intermidiaria.domain.outroContato.OutroContato;

import java.util.List;
import java.util.Optional;

public interface OutroContatoGateway {
    List<OutroContato> listar();

    Optional<OutroContato> consultarPorTelefone(String telefone);
}
