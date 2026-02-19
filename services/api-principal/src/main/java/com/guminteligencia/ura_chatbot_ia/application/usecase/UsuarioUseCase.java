package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.UsuarioExistenteException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.UsuarioNaoEncotradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.UsuarioGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioUseCase {

    private final UsuarioGateway gateway;
    private final CriptografiaUseCase criptografiaUseCase;
    private final CriptografiaJCAUseCase criptografiaJCAUseCase;

    public Usuario cadastrar(Usuario novoUsuario) {
        log.info("Cadastrando novo usuário. Usuário: {}", novoUsuario);

        Optional<Usuario> usuarioExistente = gateway.consultarPorEmail(novoUsuario.getEmail());

        if(usuarioExistente.isPresent()) {
            throw new UsuarioExistenteException();
        }

        novoUsuario.setSenha(criptografiaUseCase.criptografar(novoUsuario.getSenha()));
        novoUsuario.getConfiguracaoCrm().setAcessToken(criptografiaJCAUseCase.criptografar(novoUsuario.getConfiguracaoCrm().getAcessToken()));
        novoUsuario.setWhatsappToken(criptografiaJCAUseCase.criptografar(novoUsuario.getWhatsappToken()));
        novoUsuario.setWhatsappIdInstance(criptografiaJCAUseCase.criptografar(novoUsuario.getWhatsappIdInstance()));
        novoUsuario.setAgenteApiKey(criptografiaJCAUseCase.criptografar(novoUsuario.getAgenteApiKey()));

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

    public Usuario consultarPorEmail(String email) {
        Optional<Usuario> usuario = gateway.consultarPorEmail(email);

        if(usuario.isEmpty()) {
            throw new UsuarioNaoEncotradoException();
        }

        return usuario.get();
    }

    public Usuario consultarPorTelefoneConectado(String telefoneUsuario) {
        log.info("Consultando usuário pelo telefone conectado: {}", telefoneUsuario);
        Optional<Usuario> usuario = gateway.consultarPorTelefoneConectado(telefoneUsuario);

        if(usuario.isEmpty()) {
            throw new UsuarioNaoEncotradoException();
        }

        log.info("Usuário consultado com sucesso. {}", usuario.get());

        return usuario.get();
    }

    public List<Usuario> listar() {
        return gateway.listar();
    }
}
