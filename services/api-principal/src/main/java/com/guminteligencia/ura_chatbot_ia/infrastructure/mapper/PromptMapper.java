package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Prompt;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.PromptEntity;

public class PromptMapper {
    public static PromptEntity paraEntity(Prompt domain) {
        return PromptEntity.builder()
                .id(domain.getId())
                .usuario(UsuarioMapper.paraEntity(domain.getUsuario()))
                .titulo(domain.getTitulo())
                .prompt(domain.getPrompt())
                .build();
    }

    public static Prompt paraDomain(PromptEntity entity) {
        return Prompt.builder()
                .id(entity.getId())
                .usuario(UsuarioMapper.paraDomain(entity.getUsuario()))
                .titulo(entity.getTitulo())
                .prompt(entity.getPrompt())
                .build();
    }
}
