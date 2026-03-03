package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.BaseConhecimento;
import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.BaseConhecimentoDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ConfiguracaoCrmDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.UsuarioDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.BaseConhecimentoEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

class BaseConhecimentoMapperTest {

    private BaseConhecimento domain;
    private BaseConhecimentoDto dto;

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

        UsuarioDto usuarioDto = UsuarioDto.builder()
                .id(UUID.randomUUID())
                .nome("nome teste")
                .telefone("00000000000")
                .senha("senhateste123")
                .email("emailteste@123")
                .telefoneConectado("00000000000")
                .atributosQualificacao(Map.of("teste", new Object()))
                .configuracaoCrm(
                        ConfiguracaoCrmDto.builder()
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

        dto = BaseConhecimentoDto.builder()
                .id(UUID.randomUUID())
                .usuario(usuarioDto)
                .titulo("Titulo teste")
                .conteudo("Conteudo teste")
                .build();

    }

    @Test
    void deveRetornarDomain() {
        BaseConhecimento resultado = BaseConhecimentoMapper.paraDomain(dto);

        Assertions.assertEquals(resultado.getId(), dto.getId());
        Assertions.assertEquals(resultado.getUsuario().getId(), dto.getUsuario().getId());
        Assertions.assertEquals(resultado.getTitulo(), dto.getTitulo());
        Assertions.assertEquals(resultado.getConteudo(), dto.getConteudo());
    }

    @Test
    void deveRetornarDto() {
        BaseConhecimentoDto resultado = BaseConhecimentoMapper.paraDto(domain);

        Assertions.assertEquals(resultado.getId(), domain.getId());
        Assertions.assertEquals(resultado.getUsuario().getId(), domain.getUsuario().getId());
        Assertions.assertEquals(resultado.getTitulo(), domain.getTitulo());
        Assertions.assertEquals(resultado.getConteudo(), domain.getConteudo());
    }
}