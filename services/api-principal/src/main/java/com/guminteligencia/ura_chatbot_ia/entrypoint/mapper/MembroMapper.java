package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.outrosSetores.Membro;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.MembroDto;

public class MembroMapper {

    public static Membro paraDomain(MembroDto dto) {
        return Membro.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .telefone(dto.getTelefone())
                .usuario(UsuarioMapper.paraDomain(dto.getUsuario()))
                .build();
    }

    public static MembroDto paraDto(Membro domain) {
        return MembroDto.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .telefone(domain.getTelefone())
                .usuario(UsuarioMapper.paraDto(domain.getUsuario()))
                .build();
    }
}
