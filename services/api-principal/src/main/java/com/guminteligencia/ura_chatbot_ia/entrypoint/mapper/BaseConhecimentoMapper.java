package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.BaseConhecimento;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.BaseConhecimentoDto;

public class BaseConhecimentoMapper {
    public static BaseConhecimento paraDomain(BaseConhecimentoDto dto) {
        return BaseConhecimento.builder()
                .id(dto.getId())
                .usuario(UsuarioMapper.paraDomain(dto.getUsuario()))
                .titulo(dto.getTitulo())
                .conteudo(dto.getConteudo())
                .build();
    }

    public static BaseConhecimentoDto paraDto(BaseConhecimento domain) {
        return BaseConhecimentoDto.builder()
                .id(domain.getId())
                .usuario(UsuarioMapper.paraDto(domain.getUsuario()))
                .titulo(domain.getTitulo())
                .conteudo(domain.getConteudo())
                .build();
    }
}
