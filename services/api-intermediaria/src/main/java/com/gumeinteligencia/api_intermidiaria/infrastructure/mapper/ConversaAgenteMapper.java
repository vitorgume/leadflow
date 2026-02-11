package com.gumeinteligencia.api_intermidiaria.infrastructure.mapper;

import com.gumeinteligencia.api_intermidiaria.domain.ConversaAgente;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.ConversaAgenteEntity;

public class ConversaAgenteMapper {

    public static ConversaAgente paraDomain(ConversaAgenteEntity entity) {
        return ConversaAgente.builder()
                .id(entity.getId())
                .cliente(ClienteMapper.paraDomain(entity.getCliente()))
                .dataCriacao(entity.getDataCriacao())
                .finalizada(entity.getFinalizada())
                .dataUltimaMensagem(entity.getDataUltimaMensagem())
                .recontato(entity.getRecontato())
                .build();
    }
}
