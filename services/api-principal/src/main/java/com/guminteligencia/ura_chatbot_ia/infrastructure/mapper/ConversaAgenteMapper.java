package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConversaAgenteEntity;

public class ConversaAgenteMapper {
    public static ConversaAgenteEntity paraEntity(ConversaAgente domain) {
        return ConversaAgenteEntity.builder()
                .id(domain.getId())
                .cliente(ClienteMapper.paraEntity(domain.getCliente()))
                .vendedor(domain.getVendedor() == null ? null : VendedorMapper.paraEntity(domain.getVendedor()))
                .dataCriacao(domain.getDataCriacao())
                .finalizada(domain.getFinalizada())
                .dataUltimaMensagem(domain.getDataUltimaMensagem())
                .recontato(domain.getRecontato())
                .status(domain.getStatus())
                .membro(domain.getMembro() == null ? null : MembroMapper.paraEntity(domain.getMembro()))
                .build();
    }

    public static ConversaAgente paraDomain(ConversaAgenteEntity entity) {
        return ConversaAgente.builder()
                .id(entity.getId())
                .cliente(ClienteMapper.paraDomain(entity.getCliente()))
                .vendedor(entity.getVendedor() == null ? null : VendedorMapper.paraDomain(entity.getVendedor()))
                .dataCriacao(entity.getDataCriacao())
                .finalizada(entity.getFinalizada())
                .dataUltimaMensagem(entity.getDataUltimaMensagem())
                .recontato(entity.getRecontato())
                .status(entity.getStatus())
                .membro(entity.getMembro() == null ? null : MembroMapper.paraDomain(entity.getMembro()))
                .build();
    }

}