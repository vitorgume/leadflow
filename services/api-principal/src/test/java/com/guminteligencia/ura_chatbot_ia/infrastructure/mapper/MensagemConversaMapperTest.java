package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.MensagemConversa;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ClienteEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConversaAgenteEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.MensagemConversaEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.VendedorEntity;
import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConfiguracaoCrmEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.UsuarioEntity;

class MensagemConversaMapperTest {

    private MensagemConversa mensagemConversaDomain;
    private MensagemConversaEntity mensagemConversaEntity;

    @BeforeEach
    void setUp() {
        mensagemConversaDomain = MensagemConversa.builder()
                .id(UUID.randomUUID())
                .responsavel("usuario")
                .conteudo("teste 123")
                .data(LocalDateTime.of(2025, Month.OCTOBER, 8, 14, 41))
                .conversaAgente(ConversaAgente.builder()
                        .id(UUID.randomUUID())
                        .cliente(Cliente.builder().id(UUID.randomUUID()).usuario(Usuario.builder().id(UUID.randomUUID()).configuracaoCrm(ConfiguracaoCrm.builder().crmType(CrmType.KOMMO).build()).build()).build())
                        .vendedor(Vendedor.builder().id(1L).usuario(Usuario.builder().id(UUID.randomUUID()).configuracaoCrm(ConfiguracaoCrm.builder().crmType(CrmType.KOMMO).build()).build()).build())
                        .build()
                )
                .build();

        mensagemConversaEntity = MensagemConversaEntity.builder()
                .id(UUID.randomUUID())
                .responsavel("agente")
                .data(LocalDateTime.of(2025, Month.OCTOBER, 8, 14, 41))
                .conversaAgente(ConversaAgenteEntity.builder()
                        .id(UUID.randomUUID())
                        .cliente(ClienteEntity.builder().id(UUID.randomUUID()).usuario(UsuarioEntity.builder().id(UUID.randomUUID()).configuracaoCrm(ConfiguracaoCrmEntity.builder().crmType(CrmType.KOMMO).build()).build()).build())
                        .vendedor(VendedorEntity.builder().id(1L).usuario(UsuarioEntity.builder().id(UUID.randomUUID()).configuracaoCrm(ConfiguracaoCrmEntity.builder().crmType(CrmType.KOMMO).build()).build()).build())
                        .build()
                )
                .build();
    }

    @Test
    void deveRetornarDomainComSucesso() {
        MensagemConversa resultado = MensagemConversaMapper.paraDomain(mensagemConversaEntity);

        Assertions.assertEquals(mensagemConversaEntity.getId(), resultado.getId());
        Assertions.assertEquals(mensagemConversaEntity.getResponsavel(), resultado.getResponsavel());
        Assertions.assertEquals(mensagemConversaEntity.getConteudo(), resultado.getConteudo());
        Assertions.assertEquals(mensagemConversaEntity.getData(), resultado.getData());
        Assertions.assertEquals(mensagemConversaEntity.getConversaAgente().getId(), resultado.getConversaAgente().getId());
    }

    @Test
    void deveRetornarEntityComSucesso() {
        MensagemConversaEntity resultado = MensagemConversaMapper.paraEntity(mensagemConversaDomain);

        Assertions.assertEquals(mensagemConversaDomain.getId(), resultado.getId());
        Assertions.assertEquals(mensagemConversaDomain.getResponsavel(), resultado.getResponsavel());
        Assertions.assertEquals(mensagemConversaDomain.getConteudo(), resultado.getConteudo());
        Assertions.assertEquals(mensagemConversaDomain.getData(), resultado.getData());
        Assertions.assertEquals(mensagemConversaDomain.getConversaAgente().getId(), resultado.getConversaAgente().getId());
    }
}