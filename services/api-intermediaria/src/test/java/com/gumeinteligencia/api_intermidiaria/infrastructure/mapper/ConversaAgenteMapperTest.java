package com.gumeinteligencia.api_intermidiaria.infrastructure.mapper;

import com.gumeinteligencia.api_intermidiaria.domain.Cliente;
import com.gumeinteligencia.api_intermidiaria.domain.ConversaAgente;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.ClienteEntity;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.ConversaAgenteEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConversaAgenteMapperTest {

    private ConversaAgenteEntity conversaAgenteEntity;
    private UUID ID_CONVERSA;

    @BeforeEach
    void setUp() {
        ID_CONVERSA = UUID.randomUUID();

        ClienteEntity clienteEntity = ClienteEntity.builder()
                .id(UUID.randomUUID())
                .nome("Nome teste d")
                .telefone("5500000000000")
                .inativo(false)
                .build();

        conversaAgenteEntity = ConversaAgenteEntity.builder()
                .id(ID_CONVERSA)
                .cliente(clienteEntity)
                .dataCriacao(LocalDateTime.now())
                .finalizada(false)
                .dataUltimaMensagem(LocalDateTime.now())
                .recontato(false)
                .build();
    }

    @Test
    void deveRetornarDomainComSucesso() {
        ConversaAgente conversaAgenteDomain = ConversaAgenteMapper.paraDomain(conversaAgenteEntity);

        Assertions.assertEquals(conversaAgenteDomain.getId(), conversaAgenteEntity.getId());
        Assertions.assertEquals(conversaAgenteDomain.getCliente().getId(), conversaAgenteEntity.getCliente().getId());
        Assertions.assertEquals(conversaAgenteDomain.getDataCriacao(), conversaAgenteEntity.getDataCriacao());
        Assertions.assertFalse(conversaAgenteDomain.getFinalizada());
        Assertions.assertEquals(conversaAgenteDomain.getDataUltimaMensagem(), conversaAgenteEntity.getDataUltimaMensagem());
        Assertions.assertFalse(conversaAgenteDomain.getRecontato());
    }
}