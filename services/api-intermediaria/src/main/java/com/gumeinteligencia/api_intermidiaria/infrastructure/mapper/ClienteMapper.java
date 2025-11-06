package com.gumeinteligencia.api_intermidiaria.infrastructure.mapper;

import com.gumeinteligencia.api_intermidiaria.domain.Cliente;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.ClienteEntity;

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
}
