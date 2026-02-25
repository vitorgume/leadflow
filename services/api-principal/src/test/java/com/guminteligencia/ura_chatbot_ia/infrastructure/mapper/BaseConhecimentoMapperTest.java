package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.BaseConhecimento;
import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.BaseConhecimentoEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConfiguracaoCrmEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.UsuarioEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

class BaseConhecimentoMapperTest {

    private BaseConhecimento domain;
    private BaseConhecimentoEntity entity;

    @BeforeEach
    void setUp() {
        Usuario usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("nome teste")
                .telefone("00000000000")
                .senha("senhateste123")
                .email("emailteste@123")
                .telefoneConectado("00000000000")
                .atributosQualificacao(Map.of("teste", new Object()))
                .configuracaoCrm(
                        ConfiguracaoCrm.builder()
                                .crmType(CrmType.KOMMO)
                                .mapeamentoCampos(Map.of("teste", "teste"))
                                .idTagAtivo("id-teste")
                                .idTagAtivo("id-teste")
                                .idEtapaAtivos("id-teste")
                                .idEtapaInativos("id-teste")
                                .acessToken("acess-token-teste")
                                .build()
                )
                .mensagemDirecionamentoVendedor("mensagem-teste")
                .mensagemRecontatoG1("mensagem-teste")
                .whatsappToken("token-teste")
                .whatsappIdInstance("id-teste")
                .agenteApiKey("api-key-teste")
                .build();

        UsuarioEntity usuarioEntity = UsuarioEntity.builder()
                .id(UUID.randomUUID())
                .nome("nome teste")
                .telefone("00000000000")
                .senha("senhateste123")
                .email("emailteste@123")
                .telefoneConectado("00000000000")
                .atributosQualificacao(Map.of("teste", new Object()))
                .configuracaoCrm(
                        ConfiguracaoCrmEntity.builder()
                                .crmType(CrmType.KOMMO)
                                .mapeamentoCampos(Map.of("teste", "teste"))
                                .idTagAtivo("id-teste")
                                .idTagAtivo("id-teste")
                                .idEtapaAtivos("id-teste")
                                .idEtapaInativos("id-teste")
                                .acessToken("acess-token-teste")
                                .build()
                )
                .mensagemDirecionamentoVendedor("mensagem-teste")
                .mensagemRecontatoG1("mensagem-teste")
                .whatsappToken("token-teste")
                .whatsappIdInstance("id-teste")
                .agenteApiKey("api-key-teste")
                .build();

        domain = BaseConhecimento.builder()
                .id(UUID.randomUUID())
                .usuario(usuario)
                .titulo("Titulo teste")
                .conteudo("Conteudo teste")
                .build();

        entity = BaseConhecimentoEntity.builder()
                .id(UUID.randomUUID())
                .usuario(usuarioEntity)
                .titulo("Titulo teste")
                .conteudo("Conteudo teste")
                .build();

    }

    @Test
    void deveRetornarEntity() {
        BaseConhecimentoEntity resultado = BaseConhecimentoMapper.paraEntity(domain);

        Assertions.assertEquals(resultado.getId(), domain.getId());
        Assertions.assertEquals(resultado.getUsuario().getId(), domain.getUsuario().getId());
        Assertions.assertEquals(resultado.getTitulo(), domain.getTitulo());
        Assertions.assertEquals(resultado.getConteudo(), domain.getConteudo());
    }

    @Test
    void deveRetornarDomain() {
        BaseConhecimento resultado = BaseConhecimentoMapper.paraDomain(entity);

        Assertions.assertEquals(resultado.getId(), entity.getId());
        Assertions.assertEquals(resultado.getUsuario().getId(), entity.getUsuario().getId());
        Assertions.assertEquals(resultado.getTitulo(), entity.getTitulo());
        Assertions.assertEquals(resultado.getConteudo(), entity.getConteudo());
    }
}