package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.outrosSetores.Setor;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.SetorDto;

public class SetorMapper {

    public static Setor paraDomain(SetorDto dto) {
        return Setor.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .membros(dto.getMembros().stream().map(MembroMapper::paraDomain).toList())
                .usuario(UsuarioMapper.paraDomain(dto.getUsuario()))
                .dataCriacao(dto.getDataCriacao())
                .build();
    }

    public static SetorDto paraDto(Setor domain) {
        return SetorDto.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .descricao(domain.getDescricao())
                .membros(domain.getMembros().stream().map(MembroMapper::paraDto).toList())
                .usuario(UsuarioMapper.paraDto(domain.getUsuario()))
                .dataCriacao(domain.getDataCriacao())
                .build();
    }
}
