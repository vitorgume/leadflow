package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.VendedorEntity;

public class VendedorMapper {
    public static Vendedor paraDomain(VendedorEntity entity) {
        return Vendedor.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .inativo(entity.getInativo())
                .telefone(entity.getTelefone())
                .idVendedorCrm(entity.getIdVendedorCrm())
                .padrao(entity.getPadrao())
                .usuario(UsuarioMapper.paraDomain(entity.getUsuario()))
                .build();
    }

    public static VendedorEntity paraEntity(Vendedor domain) {
        return VendedorEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .inativo(domain.getInativo())
                .telefone(domain.getTelefone())
                .idVendedorCrm(domain.getIdVendedorCrm())
                .padrao(domain.getPadrao())
                .usuario(UsuarioMapper.paraEntity(domain.getUsuario()))
                .build();
    }
}
