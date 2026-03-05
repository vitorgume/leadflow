package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.MembroGateway;
import com.guminteligencia.ura_chatbot_ia.domain.outrosSetores.Membro;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.MembroMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.MembroRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.MembroEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MembroDataProvider implements MembroGateway {

    private final MembroRepository repository;
    private final String MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE = "Erro ao consultar membro por telefone.";
    private final String MENSAGEM_ERRO_SALVAR = "Erro ao salvar membro.";
    private final String MENSAGEM_ERRO_LISTAR = "Erro ao listar membros.";
    private final String MENSAGEM_ERRO_CONSULTAR_POR_ID = "Erro ao consultar membro por id.";
    private final String MENSAGEM_ERRO_DELETAR = "Erro ao deletar membro.";

    @Override
    public Optional<Membro> consultarPorTelefone(String telefone) {
        Optional<MembroEntity> membroEntity;

        try {
            membroEntity = repository.findByTelefone(telefone);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE, ex.getCause());
        }

        return membroEntity.map(MembroMapper::paraDomain);
    }

    @Override
    public Membro salvar(Membro novoMembro) {
        MembroEntity membroEntity = MembroMapper.paraEntity(novoMembro);

        try {
            membroEntity = repository.save(membroEntity);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_SALVAR, ex);
            throw new DataProviderException(MENSAGEM_ERRO_SALVAR, ex.getCause());
        }

        return MembroMapper.paraDomain(membroEntity);
    }

    @Override
    public List<Membro> listar(UUID idUsuario) {
        List<MembroEntity> membroEntities;

        try {
            membroEntities = repository.findByUsuario_Id(idUsuario);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_LISTAR, ex);
            throw new DataProviderException(MENSAGEM_ERRO_LISTAR, ex.getCause());
        }

        return membroEntities.stream().map(MembroMapper::paraDomain).toList();
    }

    @Override
    public Optional<Membro> consultarPorId(UUID idMembro) {
        Optional<MembroEntity> membroEntity;

        try {
            membroEntity = repository.findById(idMembro);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex.getCause());
        }

        return membroEntity.map(MembroMapper::paraDomain);
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
