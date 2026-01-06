package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.Usuario;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioGateway {
    Optional<Usuario> consultarPorId(UUID id);

    Usuario salvar(Usuario novoUsuario);

    Optional<Usuario> consultarPorTelefone(String telefone);

    void deletar(UUID id);
}
