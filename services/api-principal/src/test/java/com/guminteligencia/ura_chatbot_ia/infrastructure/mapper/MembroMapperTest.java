package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.outrosSetores.Membro;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.MembroEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.UsuarioEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MembroMapperTest {

    private Membro domain;
    private MembroEntity entity;

    @BeforeEach
    void setUp() {
        domain = Membro.builder()
                .id(UUID.randomUUID())
                .nome("Membro teste")
                .telefone("554436565988")
                .usuario(
                        Usuario.builder()
                                .id(UUID.randomUUID())
                                .nome("Usuario teste")
                                .build()
                ).build();

        entity = MembroEntity.builder()
                .id(UUID.randomUUID())
                .nome("Membro teste")
                .telefone("554436565988")
                .usuario(
                        UsuarioEntity.builder()
                                .id(UUID.randomUUID())
                                .nome("Usuario teste")
                                .build()
                ).build();
    }

    @Test
    void deveRetornarDomainComSucesso() {
        Membro resultado = MembroMapper.paraDomain(entity);

        Assertions.assertEquals(resultado.getId(), entity.getId());
        Assertions.assertEquals(resultado.getNome(), entity.getNome());
        Assertions.assertEquals(resultado.getTelefone(), entity.getTelefone());
        Assertions.assertEquals(resultado.getUsuario().getId(), entity.getUsuario().getId());
    }

    @Test
    void deveRetornarEntityComSucesso() {
        MembroEntity resultado = MembroMapper.paraEntity(domain);

        Assertions.assertEquals(resultado.getId(), domain.getId());
        Assertions.assertEquals(resultado.getNome(), domain.getNome());
        Assertions.assertEquals(resultado.getTelefone(), domain.getTelefone());
        Assertions.assertEquals(resultado.getUsuario().getId(), domain.getUsuario().getId());
    }
}