package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.outrosSetores.Membro;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.MembroDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.UsuarioDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MembroMapperTest {

    private Membro domain;
    private MembroDto dto;

    @BeforeEach
    void setUp() {
        domain = Membro.builder()
                .id(UUID.randomUUID())
                .nome("Membro teste")
                .telefone("554469878523")
                .usuario(
                        Usuario.builder()
                        .id(UUID.randomUUID())
                        .nome("Usuario teste")
                        .build()
                ).build();

        dto = MembroDto.builder()
                .id(UUID.randomUUID())
                .nome("Membro teste")
                .telefone("554469878523")
                .usuario(
                        UsuarioDto.builder()
                                .id(UUID.randomUUID())
                                .nome("Usuario teste")
                                .build()
                ).build();
    }

    @Test
    void deveRetornarDomainComSucesso() {
        Membro resultado = MembroMapper.paraDomain(dto);

        Assertions.assertEquals(resultado.getId(), dto.getId());
        Assertions.assertEquals(resultado.getNome(), dto.getNome());
        Assertions.assertEquals(resultado.getTelefone(), dto.getTelefone());
        Assertions.assertEquals(resultado.getUsuario().getId(), dto.getUsuario().getId());
    }

    @Test
    void deveRetornarDtoComSucesso() {
        MembroDto resultado = MembroMapper.paraDto(domain);

        Assertions.assertEquals(resultado.getId(), domain.getId());
        Assertions.assertEquals(resultado.getNome(), domain.getNome());
        Assertions.assertEquals(resultado.getTelefone(), domain.getTelefone());
        Assertions.assertEquals(resultado.getUsuario().getId(), domain.getUsuario().getId());
    }
}