package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.BaseConhecimento;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.BaseConhecimentoDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.UsuarioDto;

public class BaseConhecimentoMapper {
    public static BaseConhecimento paraDomain(BaseConhecimentoDto dto) {
        return BaseConhecimento.builder()
                .id(dto.getId())
                .usuario(Usuario.builder().id(dto.getUsuario().getId()).build())
                .titulo(dto.getTitulo())
                .conteudo(dto.getConteudo())
                .build();
    }

    public static BaseConhecimentoDto paraDto(BaseConhecimento domain) {
        return BaseConhecimentoDto.builder()
                .id(domain.getId())
                .usuario(UsuarioDto.builder().id(domain.getUsuario().getId()).build())
                .titulo(domain.getTitulo())
                .conteudo(domain.getConteudo())
                .build();
    }
}
