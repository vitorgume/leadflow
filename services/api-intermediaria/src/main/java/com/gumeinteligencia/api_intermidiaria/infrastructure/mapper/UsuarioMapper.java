package com.gumeinteligencia.api_intermidiaria.infrastructure.mapper;

import com.gumeinteligencia.api_intermidiaria.domain.Usuario;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.UsuarioEntity;

public class UsuarioMapper {

    public static Usuario paraDomain(UsuarioEntity entity) {
        return Usuario.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .telefone(entity.getTelefone())
                .email(entity.getEmail())
                .telefoneConectado(entity.getTelefoneConectado())
                .softwareLigado(entity.getSoftwareLigado())
                .build();
    }
}
