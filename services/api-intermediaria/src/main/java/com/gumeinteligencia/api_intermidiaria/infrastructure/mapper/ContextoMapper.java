package com.gumeinteligencia.api_intermidiaria.infrastructure.mapper;

import com.gumeinteligencia.api_intermidiaria.domain.Contexto;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.ContextoEntityLeadflow;

public class ContextoMapper {

    public static Contexto paraDomain(ContextoEntityLeadflow entity) {
        return Contexto.builder()
                .id(entity.getId())
                .mensagens(entity.getMensagens())
                .telefone(entity.getTelefone())
                .status(entity.getStatus())
                .build();
    }

    public static ContextoEntityLeadflow paraEntity(Contexto domain) {
        return ContextoEntityLeadflow.builder()
                .id(domain.getId())
                .mensagens(domain.getMensagens())
                .telefone(domain.getTelefone())
                .status(domain.getStatus())
                .build();
    }
}
