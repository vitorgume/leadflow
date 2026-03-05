package com.gumeinteligencia.api_intermidiaria.application.usecase;

import com.gumeinteligencia.api_intermidiaria.application.exceptions.UsuarioNaoEncontradoException;
import com.gumeinteligencia.api_intermidiaria.application.gateways.UsuarioGateway;
import com.gumeinteligencia.api_intermidiaria.domain.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioGateway gateway;

    public Usuario consultarPorTelefoneConectado(String telefoneConectado) {
        Optional<Usuario> usuario = gateway.consultarPorTelefoneConectado(telefoneConectado);

        if(usuario.isEmpty()) {
            throw new UsuarioNaoEncontradoException();
        }

        return usuario.get();
    }
}
