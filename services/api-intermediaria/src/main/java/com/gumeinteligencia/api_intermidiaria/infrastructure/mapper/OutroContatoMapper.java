package com.gumeinteligencia.api_intermidiaria.infrastructure.mapper;

import com.gumeinteligencia.api_intermidiaria.domain.outroContato.OutroContato;

public class OutroContatoMapper {

    public static OutroContato paraDomain(OutroContatoEntityLeadflow entity) {
        return OutroContato.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .telefone(entity.getTelefone())
                .setor(entity.getSetor())
                .build();
    }

    public static OutroContatoEntityLeadflow paraEntity(OutroContato domain) {
        return OutroContatoEntityLeadflow.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .descricao(domain.getDescricao())
                .telefone(domain.getTelefone())
                .setor(domain.getSetor())
                .build();
    }
}
