package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.SetorGateway;
import com.guminteligencia.ura_chatbot_ia.domain.outrosSetores.Setor;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.SetorMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.SetorRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.SetorEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SetorDataProvider implements SetorGateway {

    private final SetorRepository repository;
    private final String MENSAGEM_ERRO_CONSULTAR_POR_NOME = "Erro ao consultar setor por nome.";
    private final String MENSAGEM_ERRO_SALVAR = "Erro ao salvar setor.";
    private final String MENSAGEM_ERRO_LISTAR = "Erro ao listar setores.";
    private final String MENSAGEM_ERRO_CONSULTAR_POR_ID = "Erro ao consultar por id setor.";
    private final String MENSAGEM_ERRO_DELETAR = "Erro ao deletar setor.";

    @Override
    public Optional<Setor> consultarPorNome(String nome) {
        Optional<SetorEntity> setorEntity;

        try {
            setorEntity = repository.findByNome(nome);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_NOME, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_NOME, ex.getCause());
        }

        return setorEntity.map(SetorMapper::paraDomain);
    }

    @Override
    public Setor salvar(Setor novoSetor) {
        SetorEntity setorEntity = SetorMapper.paraEntity(novoSetor);

        try {
            setorEntity = repository.save(setorEntity);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_SALVAR, ex);
            throw new DataProviderException(MENSAGEM_ERRO_SALVAR, ex.getCause());
        }

        return SetorMapper.paraDomain(setorEntity);
    }

    @Override
    public List<Setor> listar(UUID idUsuario) {
        List<SetorEntity> setorEntities;

        try {
            setorEntities = repository.findByUsuario_Id(idUsuario);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_LISTAR, ex);
            throw new DataProviderException(MENSAGEM_ERRO_LISTAR, ex.getCause());
        }

        return setorEntities.stream().map(SetorMapper::paraDomain).toList();
    }

    @Override
    public Optional<Setor> consultarPorId(UUID id) {
        Optional<SetorEntity> setorEntity;

        try {
            setorEntity = repository.findById(id);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex.getCause());
        }

        return setorEntity.map(SetorMapper::paraDomain);
    }

    @Override
    public void deletar(UUID id) {
        try {
            repository.deletarById(id);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_DELETAR, ex);
            throw new DataProviderException(MENSAGEM_ERRO_DELETAR, ex.getCause());
        }
    }
}
