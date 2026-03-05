package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.outrosSetores.Membro;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.MembroEntity;

public class MembroMapper {

    public static Membro paraDomain(MembroEntity entity) {
        return Membro.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .telefone(entity.getTelefone())
                .usuario(UsuarioMapper.paraDomain(entity.getUsuario()))
                .build();
    }

    public static MembroEntity paraEntity(Membro domain) {
        return MembroEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .telefone(domain.getTelefone())
                .usuario(UsuarioMapper.paraEntity(domain.getUsuario()))
                .build();
    }
}
