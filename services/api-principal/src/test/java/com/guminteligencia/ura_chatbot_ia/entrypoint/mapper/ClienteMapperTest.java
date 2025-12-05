package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ClienteDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.guminteligencia.ura_chatbot_ia.domain.PreferenciaHorario;
import com.guminteligencia.ura_chatbot_ia.domain.TipoConsulta;

import java.util.UUID;

class ClienteMapperTest {

    private Cliente clienteDomain;

    @BeforeEach
    void setUp() {
        clienteDomain = Cliente.builder()
                .id(UUID.randomUUID())
                .nome("Nome teste")
                .telefone("5544998748377")
                .cpf("12345678900")
                .consentimentoAtendimnento(true)
                .tipoConsulta(TipoConsulta.SAUDE_CAPILAR)
                .dorDesejoPaciente("Dor ou desejo do paciente")
                .preferenciaHorario(PreferenciaHorario.TARDE)
                .inativo(false)
                .build();
    }

    @Test
    void deveRetornarDtoComSucesso() {
        ClienteDto resultado = ClienteMapper.paraDto(clienteDomain);

        Assertions.assertEquals(clienteDomain.getId(), resultado.getId());
        Assertions.assertEquals(clienteDomain.getNome(), resultado.getNome());
        Assertions.assertEquals(clienteDomain.getTelefone(), resultado.getTelefone());
        Assertions.assertEquals(clienteDomain.getCpf(), resultado.getCpf());
        Assertions.assertEquals(clienteDomain.getConsentimentoAtendimnento(), resultado.getConsentimentoAtendimnento());
        Assertions.assertEquals(clienteDomain.getTipoConsulta(), resultado.getTipoConsulta());
        Assertions.assertEquals(clienteDomain.getDorDesejoPaciente(), resultado.getDorDesejoPaciente());
        Assertions.assertEquals(clienteDomain.getPreferenciaHorario(), resultado.getPreferenciaHorario());
        Assertions.assertEquals(clienteDomain.isInativo(), resultado.isInativo());
        Assertions.assertNull(resultado.getLinkMidia());
    }
}
