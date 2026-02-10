package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.ConfiguracaoEscolhaVendedor;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.vendedor.ConfiguracaoEscolhaVendedorDto;

public class ConfiguracaoEscolhaVendedorMapper {

    public static ConfiguracaoEscolhaVendedor paraDomain(ConfiguracaoEscolhaVendedorDto dto) {
        ConfiguracaoEscolhaVendedor configuracaoEscolhaVendedor = ConfiguracaoEscolhaVendedor.builder()
                .id(dto.getId())
                .usuario(Usuario.builder().id(dto.getUsuario().getId()).build())
                .prioridade(dto.getPrioridade())
                .build();

        if(dto.getVendedores() != null) {
            configuracaoEscolhaVendedor.setVendedores(dto.getVendedores().stream().map(vendedorDto -> Vendedor.builder().id(vendedorDto.getId()).build()).toList());
        }

        if(dto.getCondicoes() != null) {
            configuracaoEscolhaVendedor.setCondicoes(dto.getCondicoes().stream().map(CondicaoMapper::paraDomain).toList());
        }

        return configuracaoEscolhaVendedor;
    }

    public static ConfiguracaoEscolhaVendedorDto paraDto(ConfiguracaoEscolhaVendedor domain) {
        return ConfiguracaoEscolhaVendedorDto.builder()
                .id(domain.getId())
                .usuario(UsuarioMapper.paraDto(domain.getUsuario()))
                .vendedores(domain.getVendedores().stream().map(VendedorMapper::paraDto).toList())
                .condicoes(domain.getCondicoes().stream().map(CondicaoMapper::paraDto).toList())
                .prioridade(domain.getPrioridade())
                .build();
    }
}
