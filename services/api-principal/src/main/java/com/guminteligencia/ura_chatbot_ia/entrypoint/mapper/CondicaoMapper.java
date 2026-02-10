package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Condicao;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.vendedor.CondicaoDto;

public class CondicaoMapper {

    public static Condicao paraDomain(CondicaoDto dto) {
        return Condicao.builder()
                .id(dto.getId())
                .campo(dto.getCampo())
                .operadorLogico(dto.getOperadorLogico())
                .valor(dto.getValor())
                .conectorLogico(dto.getConectorLogico())
                .build();
    }

    public static CondicaoDto paraDto(Condicao domain) {
        return CondicaoDto.builder()
                .id(domain.getId())
                .campo(domain.getCampo())
                .operadorLogico(domain.getOperadorLogico())
                .valor(domain.getValor())
                .conectorLogico(domain.getConectorLogico())
                .build();
    }
}
