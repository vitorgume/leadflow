package com.gumeinteligencia.api_intermidiaria.infrastructure.mapper;

import com.gumeinteligencia.api_intermidiaria.domain.Contexto;
import com.gumeinteligencia.api_intermidiaria.domain.MensagemContexto;
import com.gumeinteligencia.api_intermidiaria.domain.StatusContexto;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.ContextoEntityLeadflow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

class ContextoMapperTest {

    private Contexto contextoDomain;
    private ContextoEntityLeadflow contextoEntityLeadflow;

    @BeforeEach
    void setUp() {
        contextoDomain = Contexto.builder()
                .id(UUID.randomUUID())
                .telefone("000000000000")
                .status(StatusContexto.ATIVO)
                .mensagens(List.of(MensagemContexto.builder().mensagem("Ola").build()))
                .build();

        contextoEntityLeadflow = ContextoEntityLeadflow.builder()
                .id(UUID.randomUUID())
                .telefone("000000000000")
                .status(StatusContexto.ATIVO)
                .mensagens(List.of(MensagemContexto.builder().mensagem("Ola").build()))
                .build();
    }

    @Test
    void deveRetornarDomain() {
        Contexto resultado = ContextoMapper.paraDomain(contextoEntityLeadflow);

        Assertions.assertEquals(contextoEntityLeadflow.getId(), resultado.getId());
        Assertions.assertEquals(contextoEntityLeadflow.getTelefone(), resultado.getTelefone());
        Assertions.assertEquals(contextoEntityLeadflow.getStatus(), resultado.getStatus());
        Assertions.assertEquals(contextoEntityLeadflow.getMensagens(), resultado.getMensagens());
    }

    @Test
    void deveRetornarEntity() {
        ContextoEntityLeadflow resultado = ContextoMapper.paraEntity(contextoDomain);

        Assertions.assertEquals(contextoDomain.getId(), resultado.getId());
        Assertions.assertEquals(contextoDomain.getTelefone(), resultado.getTelefone());
        Assertions.assertEquals(contextoDomain.getStatus(), resultado.getStatus());
        Assertions.assertEquals(contextoDomain.getMensagens(), resultado.getMensagens());
    }
}
