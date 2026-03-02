package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.OutroContatoEntity;

public class OutroContatoNoSqlMapper {

    public static OutroContato paraDomain(OutroContatoEntity entity) {
        return OutroContato.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .tipoContato(entity.getTipoContato())
                .descricao(entity.getDescricao())
                .telefone(entity.getTelefone())
                .usuario(Usuario.builder().id(entity.getIdUsuario()).build())
                .build();
    }

    public static OutroContatoEntity paraEntity(OutroContato domain) {
        return OutroContatoEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .tipoContato(domain.getTipoContato())
                .descricao(domain.getDescricao())
                .telefone(domain.getTelefone())
                .idUsuario(domain.getUsuario().getId())
                .build();
    }
}
