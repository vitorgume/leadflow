package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.UsuarioGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.UsuarioMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.UsuarioRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.UsuarioEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsuarioDataProvider implements UsuarioGateway {

    private final UsuarioRepository repository;

    private final String MENSAGEM_ERRO_CONSULTAR_POR_ID = "Erro ao buscar usuário pelo id.";
    private final String MENSAGEM_ERRO_SALVAR = "Erro ao salvar usuário.";
    private final String MENSAGEM_ERRO_CONSULTAR_POR_EMAIL = "Erro ao consultar usuário pelo email.";
    private final String MENSAGEM_ERRO_DELETAR = "Erro ao deletar usuário.";
    private final String MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE_CONECTADO = "Erro ao consultar usuário pelo telefone conectado.";
    private final String MENSAGEM_ERRO_LISTAR = "Erro ao listar todos os usuários.";

    @Override
    public Optional<Usuario> consultarPorId(UUID id) {
        Optional<UsuarioEntity> usuario;

        try {
            usuario = repository.findById(id);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex.getCause());
        }

        return usuario.map(UsuarioMapper::paraDomain);
    }

    @Override
    public Usuario salvar(Usuario novoUsuario) {
        UsuarioEntity usuarioEntity = UsuarioMapper.paraEntity(novoUsuario);

        try {
            usuarioEntity = repository.save(usuarioEntity);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_SALVAR, ex);
            throw new DataProviderException(MENSAGEM_ERRO_SALVAR, ex.getCause());
        }

        return UsuarioMapper.paraDomain(usuarioEntity);
    }

    @Override
    public Optional<Usuario> consultarPorEmail(String email) {
        Optional<UsuarioEntity> usuario;

        try {
            usuario = repository.findByEmail(email);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_EMAIL, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_EMAIL, ex.getCause());
        }

        return usuario.map(UsuarioMapper::paraDomain);
    }

    @Override
    public Optional<Usuario> consultarPorTelefoneConectado(String telefoneUsuario) {
        Optional<UsuarioEntity> usuarioEntity;

        try {
            usuarioEntity = repository.findByTelefoneConectado(telefoneUsuario);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE_CONECTADO, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE_CONECTADO, ex.getCause());
        }

        return usuarioEntity.map(UsuarioMapper::paraDomain);
    }

    @Override
    public List<Usuario> listar() {
        List<UsuarioEntity> usuarioEntities;

        try {
            usuarioEntities = repository.findAll();
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_LISTAR, ex);
            throw new DataProviderException(MENSAGEM_ERRO_LISTAR, ex.getCause());
        }

        return usuarioEntities.stream().map(UsuarioMapper::paraDomain).toList();
    }

    @Override
    public void deletar(UUID id) {
        try {
            repository.deleteById(id);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_DELETAR, ex);
            throw new DataProviderException(MENSAGEM_ERRO_DELETAR, ex.getCause());
        }
    }
}
