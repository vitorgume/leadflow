package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.OutroContatoEntityLeadflow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OutroContatoMapperTest {

    private OutroContatoEntityLeadflow outroContatoEntityLeadflow;

    @BeforeEach
    void setUp() {
        outroContatoEntityLeadflow = OutroContatoEntityLeadflow.builder()
                .id(1L)
                .nome("Nome outro contato")
                .telefone("000000000000")
                .descricao("Descrição domain")
                .build();
    }

    @Test
    void deveTransformaraParaDomain() {
        OutroContato outroContatoTeste = OutroContatoMapper.paraDomain(outroContatoEntityLeadflow);

        Assertions.assertEquals(outroContatoTeste.getId(), outroContatoEntityLeadflow.getId());
        Assertions.assertEquals(outroContatoTeste.getNome(), outroContatoEntityLeadflow.getNome());
        Assertions.assertEquals(outroContatoTeste.getTelefone(), outroContatoEntityLeadflow.getTelefone());
        Assertions.assertEquals(outroContatoTeste.getDescricao(), outroContatoEntityLeadflow.getDescricao());
    }
}