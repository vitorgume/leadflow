package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.UsuarioDto;

public class UsuarioMapper {

    public static UsuarioDto paraDto(Usuario domain) {
        return UsuarioDto.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .telefone(domain.getTelefone())
                .senha(domain.getSenha())
                .email(domain.getEmail())
                .telefoneConectado(domain.getTelefoneConcectado())
                .atributosQualificacao(domain.getAtributosQualificacao())
                .configuracaoCrm(ConfiguracaoCrmMapper.paraDto(domain.getConfiguracaoCrm()))
                .build();
    }

    public static Usuario paraDomain(UsuarioDto dto) {
        return Usuario.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .telefone(dto.getTelefone())
                .senha(dto.getSenha())
                .email(dto.getEmail())
                .telefoneConcectado(dto.getTelefoneConectado())
                .atributosQualificacao(dto.getAtributosQualificacao())
                .configuracaoCrm(ConfiguracaoCrmMapper.paraDomain(dto.getConfiguracaoCrm()))
                .build();
    }
}
