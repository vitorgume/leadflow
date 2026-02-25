package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Prompt;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.PromptDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.UsuarioDto;

public class PromptMapper {
    public static Prompt paraDomain(PromptDto dto) {
        return Prompt.builder()
                .id(dto.getId())
                .usuario(Usuario.builder().id(dto.getUsuario().getId()).build())
                .titulo(dto.getTitulo())
                .prompt(dto.getPrompt())
                .build();
    }

    public static PromptDto paraDto(Prompt domain) {
        return PromptDto.builder()
                .id(domain.getId())
                .usuario(UsuarioDto.builder().id(domain.getUsuario().getId()).build())
                .titulo(domain.getTitulo())
                .prompt(domain.getPrompt())
                .build();
    }
}
