package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.OutroContatoDto;

public class OutroContatoMapper {

    public static OutroContato paraDomain(OutroContatoDto dto) {
        return OutroContato.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .telefone(dto.getTelefone())
                .descricao(dto.getDescricao())
                .tipoContato(dto.getTipoContato())
                .usuario(Usuario.builder().id(dto.getUsuario().getId()).build())
                .build();
    }

    public static OutroContatoDto paraDto(OutroContato domain) {
        return OutroContatoDto.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .telefone(domain.getTelefone())
                .descricao(domain.getDescricao())
                .tipoContato(domain.getTipoContato())
                .usuario(UsuarioMapper.paraDto(domain.getUsuario()))
                .build();
    }
}
