package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
<<<<<<< HEAD
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.OutroContatoEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.UsuarioEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConfiguracaoCrmEntity;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;

import java.util.UUID;
=======
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.OutroContatoEntityLeadflow;
>>>>>>> main
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OutroContatoMapperTest {

<<<<<<< HEAD
    private OutroContatoEntity outroContatoEntity;
    private OutroContato outroContatoDomain;
=======
    private OutroContatoEntityLeadflow outroContatoEntityLeadflow;
>>>>>>> main

    @BeforeEach
    void setUp() {
        outroContatoEntityLeadflow = OutroContatoEntityLeadflow.builder()
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
        OutroContato outroContatoTeste = OutroContatoMapper.paraDomain(outroContatoEntityLeadflow);

        Assertions.assertEquals(outroContatoTeste.getId(), outroContatoEntityLeadflow.getId());
        Assertions.assertEquals(outroContatoTeste.getNome(), outroContatoEntityLeadflow.getNome());
        Assertions.assertEquals(outroContatoTeste.getTelefone(), outroContatoEntityLeadflow.getTelefone());
        Assertions.assertEquals(outroContatoTeste.getDescricao(), outroContatoEntityLeadflow.getDescricao());
    }

    @Test
    void deveTransformarParaEntity() {
        OutroContatoEntity outroContatoTeste = OutroContatoMapper.paraEntity(outroContatoDomain);

        Assertions.assertEquals(outroContatoTeste.getId(), outroContatoEntity.getId());
        Assertions.assertEquals(outroContatoTeste.getNome(), outroContatoEntity.getNome());
        Assertions.assertEquals(outroContatoTeste.getTelefone(), outroContatoEntity.getTelefone());
        Assertions.assertEquals(outroContatoTeste.getDescricao(), outroContatoEntity.getDescricao());
    }
}