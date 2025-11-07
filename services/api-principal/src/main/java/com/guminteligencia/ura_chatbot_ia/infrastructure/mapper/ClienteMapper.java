package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ClienteEntity;

public class ClienteMapper {
    public static Cliente paraDomain(ClienteEntity entity) {
        return Cliente.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .telefone(entity.getTelefone())
                .cpf(entity.getCpf())
                .consentimentoAtendimnento(entity.getConsentimentoAtendimnento())
                .tipoConsulta(entity.getTipoConsulta())
                .dorDesejoPaciente(entity.getDorDesejoPaciente())
                .linkMidia(entity.getLinkMidia())
                .preferenciaHorario(entity.getPreferenciaHorario())
                .inativo(entity.isInativo())
                .build();
    }

    public static ClienteEntity paraEntity(Cliente domain) {
        return ClienteEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .telefone(domain.getTelefone())
                .cpf(domain.getCpf())
                .consentimentoAtendimnento(domain.getConsentimentoAtendimnento())
                .tipoConsulta(domain.getTipoConsulta())
                .dorDesejoPaciente(domain.getDorDesejoPaciente())
                .linkMidia(domain.getLinkMidia())
                .preferenciaHorario(domain.getPreferenciaHorario())
                .inativo(domain.isInativo())
                .build();
    }
}
