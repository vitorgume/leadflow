package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.OutroContatoEntitySql;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.UsuarioEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConfiguracaoCrmEntity;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;

import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OutroContatoMapperTest {

    private OutroContatoEntitySql outroContatoEntitySql;
    private OutroContato outroContatoDomain;

    @BeforeEach
    void setUp() {
        outroContatoEntitySql = OutroContatoEntitySql.builder()
                .id(1L)
                .nome("Nome outro contato")
                .telefone("000000000000")
                .descricao("Descrição domain")
                .usuario(UsuarioEntity.builder().id(UUID.randomUUID()).configuracaoCrm(ConfiguracaoCrmEntity.builder().crmType(CrmType.KOMMO).build()).build()) // Added dummy UsuarioEntity with ConfiguracaoCrmEntity
                .build();

        outroContatoDomain = OutroContato.builder()
                .id(1L)
                .nome("Nome outro contato")
                .telefone("000000000000")
                .descricao("Descrição domain")
                .usuario(Usuario.builder().id(UUID.randomUUID()).configuracaoCrm(ConfiguracaoCrm.builder().crmType(CrmType.KOMMO).build()).build()) // Added dummy UsuarioEntity with ConfiguracaoCrmEntity
                .build();
    }

    @Test
    void deveTransformaraParaDomain() {
        OutroContato outroContatoTeste = OutroContatoMapper.paraDomain(outroContatoEntitySql);

        Assertions.assertEquals(outroContatoTeste.getId(), outroContatoEntitySql.getId());
        Assertions.assertEquals(outroContatoTeste.getNome(), outroContatoEntitySql.getNome());
        Assertions.assertEquals(outroContatoTeste.getTelefone(), outroContatoEntitySql.getTelefone());
        Assertions.assertEquals(outroContatoTeste.getDescricao(), outroContatoEntitySql.getDescricao());
    }

    @Test
    void deveTransformarParaEntity() {
        OutroContatoEntitySql outroContatoTeste = OutroContatoMapper.paraEntity(outroContatoDomain);

        Assertions.assertEquals(outroContatoTeste.getId(), outroContatoEntitySql.getId());
        Assertions.assertEquals(outroContatoTeste.getNome(), outroContatoEntitySql.getNome());
        Assertions.assertEquals(outroContatoTeste.getTelefone(), outroContatoEntitySql.getTelefone());
        Assertions.assertEquals(outroContatoTeste.getDescricao(), outroContatoEntitySql.getDescricao());
    }
}