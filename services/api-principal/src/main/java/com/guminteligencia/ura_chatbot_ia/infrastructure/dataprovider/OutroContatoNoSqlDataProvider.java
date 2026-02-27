package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.OutroContatoNoSqlGateway;
import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.OutroContatoNoSqlMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.OutroContatoRepositoryNoSql;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.OutroContatoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutroContatoNoSqlDataProvider implements OutroContatoNoSqlGateway {

    private final OutroContatoRepositoryNoSql repository;

    private static final String MENSAGEM_ERRO_SALVAR = "Erro ao salvar outro contato NoSql";
    private static final String MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE_E_USUARIO = "Erro ao consultar outro contato por telefone e usuário.";
    private static final String MENSAGEM_ERRO_DELETAR = "Erro ao deletar outro contato NoSql.";

    @Override
    public OutroContato salvar(OutroContato novoOutroContato) {
        OutroContatoEntity outroContatoEntity = OutroContatoNoSqlMapper.paraEntity(novoOutroContato);

        try {
            outroContatoEntity = repository.salvar(outroContatoEntity);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_SALVAR, ex);
            throw new DataProviderException(MENSAGEM_ERRO_SALVAR, ex.getCause());
        }

        return OutroContatoNoSqlMapper.paraDomain(outroContatoEntity);
    }

    @Override
    public Optional<OutroContato> consultarPorTelefoneEUsuario(String telefone, UUID idUsuario) {
        Optional<OutroContatoEntity> outroContato;

        try {
            outroContato = repository.consultarPorTelefoneEUsuario(telefone, idUsuario.toString());
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE_E_USUARIO, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE_E_USUARIO, ex.getCause());
        }

        return outroContato.map(OutroContatoNoSqlMapper::paraDomain);
    }

    @Override
    public void deletar(UUID id) {
        try {
            repository.deletar(id);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_DELETAR, ex);
            throw new DataProviderException(MENSAGEM_ERRO_DELETAR, ex.getCause());
        }
    }
}
