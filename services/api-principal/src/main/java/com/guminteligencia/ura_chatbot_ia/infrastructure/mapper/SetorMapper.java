package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.outrosSetores.Setor;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.SetorEntity;

public class SetorMapper {

    public static Setor paraDomain(SetorEntity entity) {
        return Setor.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .membros(entity.getMembros().stream().map(MembroMapper::paraDomain).toList())
                .dataCriacao(entity.getDataCriacao())
                .usuario(UsuarioMapper.paraDomain(entity.getUsuario()))
                .build();
    }

    public static SetorEntity paraEntity(Setor domain) {
        return SetorEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .descricao(domain.getDescricao())
                .membros(domain.getMembros().stream().map(MembroMapper::paraEntity).toList())
                .dataCriacao(domain.getDataCriacao())
                .usuario(UsuarioMapper.paraEntity(domain.getUsuario()))
                .build();
    }
}
