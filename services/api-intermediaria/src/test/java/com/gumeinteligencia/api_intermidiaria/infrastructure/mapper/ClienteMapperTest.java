package com.gumeinteligencia.api_intermidiaria.infrastructure.mapper;

import com.gumeinteligencia.api_intermidiaria.domain.Cliente;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.ClienteEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClienteMapperTest {

    private ClienteEntity clienteEntity;

    @BeforeEach
    void setUp() {
        clienteEntity = ClienteEntity.builder()
                .id(UUID.randomUUID())
                .nome("Nome teste d")
                .telefone("5500000000000")
                .inativo(false)
                .build();
    }

    @Test
    void deveRetornarDomainComSeucesso() {
        Cliente resultado = ClienteMapper.paraDomain(clienteEntity);

        Assertions.assertEquals(resultado.getId(), clienteEntity.getId());
        Assertions.assertEquals(resultado.getNome(), clienteEntity.getNome());
        Assertions.assertEquals(resultado.getTelefone(), clienteEntity.getTelefone());
        Assertions.assertFalse(resultado.isInativo());
    }
}