package com.gumeinteligencia.api_intermidiaria.infrastructure.mapper;

import com.gumeinteligencia.api_intermidiaria.domain.Cliente;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.ClienteEntity;

public class ClienteMapper {


    public static Cliente paraDomain(ClienteEntity entity) {
        return Cliente.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .telefone(entity.getTelefone())
                .atributosQualificacao(entity.getAtributosQualificacao())
                .inativo(entity.isInativo())
                .build();
    }
}
