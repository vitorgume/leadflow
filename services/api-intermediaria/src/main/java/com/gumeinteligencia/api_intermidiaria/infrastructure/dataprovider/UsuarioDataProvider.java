package com.gumeinteligencia.api_intermidiaria.infrastructure.dataprovider;

import com.gumeinteligencia.api_intermidiaria.application.gateways.UsuarioGateway;
import com.gumeinteligencia.api_intermidiaria.domain.Usuario;
import com.gumeinteligencia.api_intermidiaria.infrastructure.exceptions.DataProviderException;
import com.gumeinteligencia.api_intermidiaria.infrastructure.mapper.UsuarioMapper;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.UsuarioRepository;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.UsuarioEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class UsuarioDataProvider implements UsuarioGateway {

    private final UsuarioRepository repository;
    private final String MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE_CONECTADO = "Erro ao consultar usuário pelo telefone conectado.";

    @Override
    public Optional<Usuario> consultarPorTelefoneConectado(String telefoneConectado) {
        Optional<UsuarioEntity> usuarioEntity;

        try {
            usuarioEntity = repository.findByTelefoneConectado(telefoneConectado);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE_CONECTADO, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE_CONECTADO, ex.getCause());
        }

        return usuarioEntity.map(UsuarioMapper::paraDomain);
    }
}
