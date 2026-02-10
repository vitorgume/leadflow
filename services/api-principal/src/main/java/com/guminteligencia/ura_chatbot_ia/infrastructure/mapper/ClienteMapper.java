package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ClienteEntity;

public class ClienteMapper {
    public static Cliente paraDomain(ClienteEntity entity) {
        return Cliente.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .telefone(entity.getTelefone())
                .atributosQualificacao(entity.getAtributosQualificacao())
                .inativo(entity.isInativo())
                .usuario(UsuarioMapper.paraDomain(entity.getUsuario()))
                .build();
    }

    public static ClienteEntity paraEntity(Cliente domain) {
        return ClienteEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .telefone(domain.getTelefone())
                .atributosQualificacao(domain.getAtributosQualificacao())
                .inativo(domain.isInativo())
                .usuario(UsuarioMapper.paraEntity(domain.getUsuario()))
                .build();
    }
}
