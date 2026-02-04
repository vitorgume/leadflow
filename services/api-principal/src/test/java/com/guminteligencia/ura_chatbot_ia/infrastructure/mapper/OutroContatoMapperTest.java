package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.OutroContatoEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.UsuarioEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConfiguracaoCrmEntity;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;

import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OutroContatoMapperTest {

    private OutroContatoEntity outroContatoEntity;

    @BeforeEach
    void setUp() {
        outroContatoEntity = OutroContatoEntity.builder()
                .id(1L)
                .nome("Nome outro contato")
                .telefone("000000000000")
                .descricao("Descrição domain")
                .usuario(UsuarioEntity.builder().id(UUID.randomUUID()).configuracaoCrm(ConfiguracaoCrmEntity.builder().crmType(CrmType.KOMMO).build()).build()) // Added dummy UsuarioEntity with ConfiguracaoCrmEntity
                .build();
    }

    @Test
    void deveTransformaraParaDomain() {
        OutroContato outroContatoTeste = OutroContatoMapper.paraDomain(outroContatoEntity);

        Assertions.assertEquals(outroContatoTeste.getId(), outroContatoEntity.getId());
        Assertions.assertEquals(outroContatoTeste.getNome(), outroContatoEntity.getNome());
        Assertions.assertEquals(outroContatoTeste.getTelefone(), outroContatoEntity.getTelefone());
        Assertions.assertEquals(outroContatoTeste.getDescricao(), outroContatoEntity.getDescricao());
    }
}