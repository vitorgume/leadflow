package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Prompt;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.PromptDto;

public class PromptMapper {
    public static Prompt paraDomain(PromptDto dto) {
        return Prompt.builder()
                .id(dto.getId())
                .usuario(UsuarioMapper.paraDomain(dto.getUsuario()))
                .titulo(dto.getTitulo())
                .prompt(dto.getPrompt())
                .build();
    }

    public static PromptDto paraDto(Prompt domain) {
        return PromptDto.builder()
                .id(domain.getId())
                .usuario(UsuarioMapper.paraDto(domain.getUsuario()))
                .titulo(domain.getTitulo())
                .prompt(domain.getPrompt())
                .build();
    }
}
