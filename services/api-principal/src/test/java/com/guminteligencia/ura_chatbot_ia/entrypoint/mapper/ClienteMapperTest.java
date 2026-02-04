package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ClienteDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;

import java.util.UUID;

class ClienteMapperTest {

    private Cliente clienteDomain;

    @BeforeEach
    void setUp() {
        clienteDomain = Cliente.builder()
                .id(UUID.randomUUID())
                .nome("Nome teste")
                .telefone("5544998748377")
                .inativo(false)
                .usuario(Usuario.builder()
                        .id(UUID.randomUUID())
                        .configuracaoCrm(ConfiguracaoCrm.builder().crmType(CrmType.KOMMO).build())
                        .build()) // Added dummy Usuario with ConfiguracaoCrm
                .build();
    }

    @Test
    void deveRetornarDtoComSucesso() {
        ClienteDto resultado = ClienteMapper.paraDto(clienteDomain);

        Assertions.assertEquals(clienteDomain.getId(), resultado.getId());
        Assertions.assertEquals(clienteDomain.getNome(), resultado.getNome());
        Assertions.assertEquals(clienteDomain.getTelefone(), resultado.getTelefone());
        Assertions.assertEquals(clienteDomain.isInativo(), resultado.isInativo());
    }
}
