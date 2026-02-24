package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.BaseConhecimentoGateway;
import com.guminteligencia.ura_chatbot_ia.domain.BaseConhecimento;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.BaseConhecimentoMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.BaseConhecimentoRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.BaseConhecimentoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BaseConhecimentoDataProvider implements BaseConhecimentoGateway {

    private final BaseConhecimentoRepository repository;

    public final String MENSAGEM_ERRO_SALVAR_BaseConhecimento = "Erro ao salvar nova base conhecimento.";
    public final String MENSAGEM_ERRO_LISTAR_POR_USUARIO = "Erro ao listar base de conhecimentos por usuário.";
    public final String MENSAGEM_ERRO_CONSULTAR_POR_ID = "Erro ao consultar base conhecimento por id.";
    public final String MENSAGEM_ERRO_DELETAR_POR_ID = "Erro ao deletar base conhecimento.";

    @Override
    public BaseConhecimento salvar(BaseConhecimento novoBaseConhecimento) {
        BaseConhecimentoEntity BaseConhecimentoEntity = BaseConhecimentoMapper.paraEntity(novoBaseConhecimento);

        try {
            BaseConhecimentoEntity = repository.save(BaseConhecimentoEntity);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_SALVAR_BaseConhecimento, ex);
            throw new DataProviderException(MENSAGEM_ERRO_SALVAR_BaseConhecimento, ex.getCause());
        }

        return BaseConhecimentoMapper.paraDomain(BaseConhecimentoEntity);
    }

    @Override
    public List<BaseConhecimento> listar(UUID idUsuario) {
        List<BaseConhecimentoEntity> BaseConhecimentoEntities;

        try {
            BaseConhecimentoEntities = repository.findByUsuario_Id(idUsuario);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_LISTAR_POR_USUARIO, ex);
            throw new DataProviderException(MENSAGEM_ERRO_LISTAR_POR_USUARIO, ex.getCause());
        }

        return BaseConhecimentoEntities.stream().map(BaseConhecimentoMapper::paraDomain).toList();
    }

    @Override
    public Optional<BaseConhecimento> consultarPorId(UUID idBaseConhecimento) {
        Optional<BaseConhecimentoEntity> BaseConhecimentoEntity;

        try {
            BaseConhecimentoEntity = repository.findById(idBaseConhecimento);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex.getCause());
        }

        return BaseConhecimentoEntity.map(BaseConhecimentoMapper::paraDomain);
    }

    @Override
    public void deletar(UUID idBaseConhecimento) {
        try {
            repository.deleteById(idBaseConhecimento);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_DELETAR_POR_ID, ex);
            throw new DataProviderException(MENSAGEM_ERRO_DELETAR_POR_ID, ex.getCause());
        }
    }
}
