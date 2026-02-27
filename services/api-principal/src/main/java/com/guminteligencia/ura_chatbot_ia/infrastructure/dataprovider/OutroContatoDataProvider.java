package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.OutroContatoGateway;
import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import com.guminteligencia.ura_chatbot_ia.domain.TipoContato;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.OutroContatoMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.OutroContatoRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.OutroContatoEntitySql;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutroContatoDataProvider implements OutroContatoGateway {

    private final String MENSAGEM_ERRO_CONSULTAR_POR_NOME = "Erro ao consultar por nome outro contato.";
    private final String MENSAGEM_ERRO_CONSULTAR_POR_TIPO = "Erro ao consultra por tipo outro contato.";
    private final String MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE = "Erro ao consultar por telefone outro contato.";
    private final String MENSAGEM_ERRO_SALVAR = "Erro ao salvar outro contato.";
    private final String MENSAGEM_ERRO_CONSULTAR_POR_ID = "Erro ao consultar por id outro contato.";
    private final String MENSAGEM_ERRO_LISTAR_POR_USUARIO = "Erro ao listar por usuario outro contato.";
    private final String MENSAGEM_ERRO_DELETAR = "Erro ao deletar por id outro contato.";

    private final OutroContatoRepository repository;

    @Override
    public Optional<OutroContato> consultarPorNome(String nome) {
        Optional<OutroContatoEntitySql> outroContatoEntity;

        try {
            outroContatoEntity = repository.findByNome(nome);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_NOME, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_NOME, ex.getCause());
        }

        return outroContatoEntity.map(OutroContatoMapper::paraDomain);
    }

    @Override
    public List<OutroContato> consultarPorTipo(TipoContato tipo, UUID idUsuario) {
        List<OutroContatoEntitySql> outroContatoEntities;

        try {
            outroContatoEntities = repository.findByTipoContatoAndUsuario_Id(tipo, idUsuario);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_TIPO, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_TIPO, ex.getCause());
        }

        return outroContatoEntities.stream().map(OutroContatoMapper::paraDomain).toList();
    }

    @Override
    public Optional<OutroContato> consultarPorTelefoneEUsuario(String telefone, UUID idUsuario) {
        Optional<OutroContatoEntitySql> outroContatoEntity;

        try {
            outroContatoEntity = repository.findByTelefoneAndUsuario_Id(telefone, idUsuario);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE, ex.getCause());
        }

        return outroContatoEntity.map(OutroContatoMapper::paraDomain);
    }

    @Override
    public OutroContato salvar(OutroContato novoOutroContato) {
        OutroContatoEntitySql outroContatoEntitySql = OutroContatoMapper.paraEntity(novoOutroContato);

        try {
            outroContatoEntitySql = repository.save(outroContatoEntitySql);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_SALVAR, ex);
            throw new DataProviderException(MENSAGEM_ERRO_SALVAR, ex.getCause());
        }

        return OutroContatoMapper.paraDomain(outroContatoEntitySql);
    }

    @Override
    public Optional<OutroContato> consultarPorId(Long idOutroContato) {
        Optional<OutroContatoEntitySql> outroContatoEntity;

        try {
            outroContatoEntity = repository.findById(idOutroContato);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex.getCause());
        }

        return outroContatoEntity.map(OutroContatoMapper::paraDomain);
    }

    @Override
    public Page<OutroContato> listar(Pageable pageable, UUID idUsuario) {
        Page<OutroContatoEntitySql> outroContatoEntityPage;

        try {
            outroContatoEntityPage = repository.findByUsuario_Id(pageable, idUsuario);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_LISTAR_POR_USUARIO, ex);
            throw new DataProviderException(MENSAGEM_ERRO_LISTAR_POR_USUARIO, ex.getCause());
        }

        return outroContatoEntityPage.map(OutroContatoMapper::paraDomain);
    }

    @Override
    public void deletar(Long id) {
        try {
            repository.deleteById(id);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_DELETAR, ex);
            throw new DataProviderException(MENSAGEM_ERRO_DELETAR, ex.getCause());
        }
    }
}
