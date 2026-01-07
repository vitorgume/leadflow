package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.Usuario;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioGateway {
    Optional<Usuario> consultarPorId(UUID id);

    Usuario salvar(Usuario novoUsuario);

    void deletar(UUID id);

    Optional<Usuario> consultarPorEmail(String email);

    Optional<Usuario> consultarPorTelefoneConectado(String telefoneUsuario);
}
