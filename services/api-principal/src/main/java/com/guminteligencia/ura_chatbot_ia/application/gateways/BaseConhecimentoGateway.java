package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.BaseConhecimento;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BaseConhecimentoGateway {
    BaseConhecimento salvar(BaseConhecimento novoBaseConhecimento);

    List<BaseConhecimento> listar(UUID idUsuario);

    void deletar(UUID idBaseConhecimento);

    Optional<BaseConhecimento> consultarPorId(UUID idBaseConhecimento);
}
