package com.gumeinteligencia.api_intermidiaria.application.gateways;

import com.gumeinteligencia.api_intermidiaria.domain.Usuario;

import java.util.Optional;

public interface UsuarioGateway {
    Optional<Usuario> consultarPorTelefoneConectado(String telefoneConectado);
}
