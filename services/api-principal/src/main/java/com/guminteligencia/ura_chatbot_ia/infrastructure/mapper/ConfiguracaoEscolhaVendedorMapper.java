package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.vendedor.ConfiguracaoEscolhaVendedor;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.ConfiguracaoEscolhaVendedorEntity;

public class ConfiguracaoEscolhaVendedorMapper {

    public static ConfiguracaoEscolhaVendedor paraDomain(ConfiguracaoEscolhaVendedorEntity entity) {
        return ConfiguracaoEscolhaVendedor.builder()
                .id(entity.getId())
                .usuario(UsuarioMapper.paraDomain(entity.getUsuario()))
                .vendedor(VendedorMapper.paraDomain(entity.getVendedor()))
                .condicoes(entity.getCondicoes().stream().map(CondicaoMapper::paraDomain).toList())
                .build();
    }

    public static ConfiguracaoEscolhaVendedorEntity paraEntity(ConfiguracaoEscolhaVendedor domain) {
        return ConfiguracaoEscolhaVendedorEntity.builder()
                .id(domain.getId())
                .usuario(UsuarioMapper.paraEntity(domain.getUsuario()))
                .vendedor(VendedorMapper.paraEntity(domain.getVendedor()))
                .condicoes(domain.getCondicoes().stream().map(CondicaoMapper::paraEntity).toList())
                .build();
    }
}
