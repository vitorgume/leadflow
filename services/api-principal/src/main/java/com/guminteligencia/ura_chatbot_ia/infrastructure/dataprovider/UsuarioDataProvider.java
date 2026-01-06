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

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsuarioDataProvider implements UsuarioGateway {

    private final UsuarioRepository repository;

    private final String MENSAGEM_ERRO_CONSULTAR_POR_ID = "Erro ao buscar usuário pelo id.";
    private final String MENSAGEM_ERRO_SALVAR = "Erro ao salvar usuário.";
    private final String MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE = "Erro ao consultar usuário pelo telefone.";
    private final String MENSAGEM_ERRO_DELETAR = "Erro ao deletar usuário.";

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
    public Optional<Usuario> consultarPorTelefone(String telefone) {
        Optional<UsuarioEntity> usuario;

        try {
            usuario = repository.findByTelefone(telefone);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE, ex.getCause());
        }

        return usuario.map(UsuarioMapper::paraDomain);
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
