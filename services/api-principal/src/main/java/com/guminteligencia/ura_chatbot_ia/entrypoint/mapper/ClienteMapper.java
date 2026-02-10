package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ClienteDto;

public class ClienteMapper {

    public static ClienteDto paraDto(Cliente cliente) {
        return ClienteDto.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .telefone(cliente.getTelefone())
                .atributosQualificacao(cliente.getAtributosQualificacao())
                .inativo(cliente.isInativo())
                .usuario(UsuarioMapper.paraDto(cliente.getUsuario()))
                .build();
    }
}
