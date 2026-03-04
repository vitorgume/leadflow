package com.gumeinteligencia.api_intermidiaria.infrastructure.mapper;

import com.gumeinteligencia.api_intermidiaria.domain.Usuario;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.UsuarioEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioMapperTest {

    private UsuarioEntity usuarioEntity;

    @BeforeEach
    void setUp() {
         usuarioEntity = UsuarioEntity.builder()
                 .id(UUID.randomUUID())
                 .nome("User teste")
                 .email("emailteste@gmail.com")
                 .softwareLigado(true)
                 .telefoneConectado("554498748352")
                 .telefone("554432154874")
                 .build();
    }

    @Test
    void deveRetornarDomain() {
        Usuario result = UsuarioMapper.paraDomain(usuarioEntity);

        Assertions.assertEquals(result.getId(), usuarioEntity.getId());
        Assertions.assertEquals(result.getEmail(), usuarioEntity.getEmail());
        Assertions.assertTrue(result.getSoftwareLigado());
        Assertions.assertEquals(result.getTelefoneConectado(), usuarioEntity.getTelefoneConectado());
        Assertions.assertEquals(result.getTelefone(), usuarioEntity.getTelefone());
    }
}