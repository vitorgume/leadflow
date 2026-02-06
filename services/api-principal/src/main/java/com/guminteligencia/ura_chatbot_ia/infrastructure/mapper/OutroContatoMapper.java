package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
<<<<<<< HEAD
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.OutroContatoEntity;
=======
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.OutroContatoEntityLeadflow;
>>>>>>> main

public class OutroContatoMapper {
    public static OutroContato paraDomain(OutroContatoEntityLeadflow entity) {
        return OutroContato.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .telefone(entity.getTelefone())
                .descricao(entity.getDescricao())
                .tipoContato(entity.getTipoContato())
                .usuario(UsuarioMapper.paraDomain(entity.getUsuario()))
                .build();
    }

    public static OutroContatoEntity paraEntity(OutroContato domain) {
        return OutroContatoEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .telefone(domain.getTelefone())
                .descricao(domain.getDescricao())
                .tipoContato(domain.getTipoContato())
                .usuario(UsuarioMapper.paraEntity(domain.getUsuario()))
                .build();
    }
}
