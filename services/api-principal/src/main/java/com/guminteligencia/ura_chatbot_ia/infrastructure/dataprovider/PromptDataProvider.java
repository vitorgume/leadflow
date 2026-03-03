package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.PromptGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Prompt;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.PromptMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.PromptRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.PromptEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PromptDataProvider implements PromptGateway {

    private final PromptRepository repository;

    public final String MENSAGEM_ERRO_SALVAR_PROMPT = "Erro ao salvar novo prompt.";
    public final String MENSAGEM_ERRO_LISTAR_POR_USUARIO = "Erro ao listar prompts por usuário.";
    public final String MENSAGEM_ERRO_CONSULTAR_POR_ID = "Erro ao consultar prompt por id.";
    public final String MENSAGEM_ERRO_DELETAR_POR_ID = "Erro ao deletar prompt.";

    @Override
    public Prompt salvar(Prompt novoPrompt) {
        PromptEntity promptEntity = PromptMapper.paraEntity(novoPrompt);

        try {
            promptEntity = repository.save(promptEntity);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_SALVAR_PROMPT, ex);
            throw new DataProviderException(MENSAGEM_ERRO_SALVAR_PROMPT, ex.getCause());
        }

        return PromptMapper.paraDomain(promptEntity);
    }

    @Override
    public List<Prompt> listar(UUID idUsuario) {
        List<PromptEntity> promptEntities;

        try {
            promptEntities = repository.findByUsuario_Id(idUsuario);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_LISTAR_POR_USUARIO, ex);
            throw new DataProviderException(MENSAGEM_ERRO_LISTAR_POR_USUARIO, ex.getCause());
        }

        return promptEntities.stream().map(PromptMapper::paraDomain).toList();
    }

    @Override
    public Optional<Prompt> consultarPorId(UUID idPrompt) {
        Optional<PromptEntity> promptEntity;

        try {
            promptEntity = repository.findById(idPrompt);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex.getCause());
        }

        return promptEntity.map(PromptMapper::paraDomain);
    }

    @Override
    public void deletar(UUID idPrompt) {
        try {
            repository.deleteById(idPrompt);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_DELETAR_POR_ID, ex);
            throw new DataProviderException(MENSAGEM_ERRO_DELETAR_POR_ID, ex.getCause());
        }
    }
}
