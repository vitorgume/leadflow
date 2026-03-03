package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.BaseConhecimento;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.BaseConhecimentoEntity;

public class BaseConhecimentoMapper {
    public static BaseConhecimentoEntity paraEntity(BaseConhecimento domain) {
        return BaseConhecimentoEntity.builder()
                .id(domain.getId())
                .usuario(UsuarioMapper.paraEntity(domain.getUsuario()))
                .titulo(domain.getTitulo())
                .conteudo(domain.getConteudo())
                .build();
    }

    public static BaseConhecimento paraDomain(BaseConhecimentoEntity entity) {
        return BaseConhecimento.builder()
                .id(entity.getId())
                .usuario(UsuarioMapper.paraDomain(entity.getUsuario()))
                .titulo(entity.getTitulo())
                .conteudo(entity.getConteudo())
                .build();
    }
}
