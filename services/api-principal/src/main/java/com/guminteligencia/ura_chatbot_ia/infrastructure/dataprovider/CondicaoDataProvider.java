package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.CondicaoGateway;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Condicao;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.CondicaoMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.CondicaoRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.CondicaoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class CondicaoDataProvider implements CondicaoGateway {

    private final CondicaoRepository repository;
    private static final String MENSAGME_ERRO_SALVAR_CONDICAO = "Erro ao salvar condição.";
    private static final String MENSAGEM_ERRO_CONSULTAR_POR_ID = "Erro ao consultar condição por id.";
    private static final String MENSAGEM_ERRO_DELETAR_CONDICAO = "Erro ao deletar condição.";

    @Override
    public Condicao salvar(Condicao condicao) {
        CondicaoEntity condicaoEntity = CondicaoMapper.paraEntity(condicao);

        try {
            condicaoEntity = repository.save(condicaoEntity);
        } catch (Exception ex) {
            log.error(MENSAGME_ERRO_SALVAR_CONDICAO, ex);
            throw new DataProviderException(MENSAGME_ERRO_SALVAR_CONDICAO, ex.getCause());
        }

        return CondicaoMapper.paraDomain(condicaoEntity);
    }

    @Override
    public Optional<Condicao> consultarPorId(UUID id) {
        Optional<CondicaoEntity> condicaoEntity;

        try {
            condicaoEntity = repository.findById(id);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex.getCause());
        }

        return condicaoEntity.map(CondicaoMapper::paraDomain);
    }

    @Override
    public void deletar(UUID id) {
        try {
            repository.deleteById(id);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_DELETAR_CONDICAO, ex);
            throw new DataProviderException(MENSAGEM_ERRO_DELETAR_CONDICAO, ex.getCause());
        }
    }
}
