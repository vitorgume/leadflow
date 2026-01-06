package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.UsuarioExistenteException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.UsuarioNaoEncotradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.UsuarioGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioUseCase {

    private final UsuarioGateway gateway;

    public Usuario cadastrar(Usuario novoUsuario) {
        log.info("Cadastrando novo usuário. Usuário: {}", novoUsuario);

        Optional<Usuario> usuarioExistente = gateway.consultarPorTelefone(novoUsuario.getTelefone());

        if(usuarioExistente.isPresent()) {
            throw new UsuarioExistenteException();
        }

        Usuario usuarioSalvo = gateway.salvar(novoUsuario);

        log.info("Novo usuário cadastrado com sucesso. Usuário: {}", usuarioSalvo);

        return usuarioSalvo;
    }

    public Usuario consultarPorId(UUID id) {
        Optional<Usuario> usuario = gateway.consultarPorId(id);

        if(usuario.isEmpty()) {
            throw new UsuarioNaoEncotradoException();
        }

        return usuario.get();
    }

    public void deletar(UUID id) {
        this.consultarPorId(id);
        gateway.deletar(id);
    }
}
