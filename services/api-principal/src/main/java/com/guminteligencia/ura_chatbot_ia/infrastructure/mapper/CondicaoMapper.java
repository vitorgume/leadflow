package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Condicao;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.CondicaoEntity;

public class CondicaoMapper {

    public static Condicao paraDomain(CondicaoEntity entity) {
        return Condicao.builder()
                .id(entity.getId())
                .campo(entity.getCampo())
                .operadorLogico(entity.getOperadorLogico())
                .valor(entity.getValor())
                .conectorLogico(entity.getConectorLogico())
                .build();
    }

    public static CondicaoEntity paraEntity(Condicao domain) {
        return CondicaoEntity.builder()
                .id(domain.getId())
                .campo(domain.getCampo())
                .operadorLogico(domain.getOperadorLogico())
                .valor(domain.getValor())
                .conectorLogico(domain.getConectorLogico())
                .build();
    }
}
