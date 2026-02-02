package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.vendedor.ConfiguracaoEscolhaVendedor;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.ConfiguracaoEscolhaVendedorEntity;

public class ConfiguracaoEscolhaVendedorMapper {

    public static ConfiguracaoEscolhaVendedor paraDomain(ConfiguracaoEscolhaVendedorEntity entity) {
        return ConfiguracaoEscolhaVendedor.builder()
                .id(entity.getId())
                .usuario(UsuarioMapper.paraDomain(entity.getUsuario()))
                .vendedores(entity.getVendedores().stream().map(VendedorMapper::paraDomain).toList())
                .condicoes(entity.getCondicoes().stream().map(CondicaoMapper::paraDomain).toList())
                .prioridade(entity.getPrioridade())
                .build();
    }

    public static ConfiguracaoEscolhaVendedorEntity paraEntity(ConfiguracaoEscolhaVendedor domain) {
        return ConfiguracaoEscolhaVendedorEntity.builder()
                .id(domain.getId())
                .usuario(UsuarioMapper.paraEntity(domain.getUsuario()))
                .vendedores(domain.getVendedores().stream().map(VendedorMapper::paraEntity).toList())
                .condicoes(domain.getCondicoes().stream().map(CondicaoMapper::paraEntity).toList())
                .prioridade(domain.getPrioridade())
                .build();
    }
}
