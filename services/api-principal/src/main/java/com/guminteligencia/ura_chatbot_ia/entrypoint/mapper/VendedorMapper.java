package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.VendedorDto;

public class VendedorMapper {

    public static Vendedor paraDomain(VendedorDto dto) {
        return Vendedor.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .telefone(dto.getTelefone())
                .inativo(dto.getInativo())
                .prioridade(dto.getPrioridade())
                .idVendedorCrm(dto.getIdVendedorCrm())
                .padrao(dto.getPadrao())
                .build();
    }

    public static VendedorDto paraDto(Vendedor domain) {
        return VendedorDto.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .telefone(domain.getTelefone())
                .inativo(domain.getInativo())
                .prioridade(domain.getPrioridade())
                .idVendedorCrm(domain.getIdVendedorCrm())
                .padrao(domain.getPadrao())
                .build();
    }
}
