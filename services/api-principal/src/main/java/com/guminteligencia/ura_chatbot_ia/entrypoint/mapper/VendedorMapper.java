package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.vendedor.VendedorDto;

public class VendedorMapper {

    public static Vendedor paraDomain(VendedorDto dto) {
        Vendedor vendedor = Vendedor.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .telefone(dto.getTelefone())
                .inativo(dto.getInativo())
                .idVendedorCrm(dto.getIdVendedorCrm())
                .padrao(dto.getPadrao())
                .build();

        if(dto.getUsuario() != null) {
            vendedor.setUsuario(Usuario.builder().id(dto.getUsuario().getId()).build());
        }

        return vendedor;
    }

    public static VendedorDto paraDto(Vendedor domain) {
        return VendedorDto.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .telefone(domain.getTelefone())
                .inativo(domain.getInativo())
                .idVendedorCrm(domain.getIdVendedorCrm())
                .padrao(domain.getPadrao())
                .build();
    }
}
