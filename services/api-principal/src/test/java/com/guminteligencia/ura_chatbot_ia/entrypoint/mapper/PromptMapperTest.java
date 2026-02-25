package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import com.guminteligencia.ura_chatbot_ia.domain.Prompt;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ConfiguracaoCrmDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.PromptDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.UsuarioDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConfiguracaoCrmEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.PromptEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.UsuarioEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

class PromptMapperTest {

    private Prompt domain;
    private PromptDto dto;

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


        domain = Prompt.builder()
                .id(UUID.randomUUID())
                .usuario(usuario)
                .titulo("Teste titulo")
                .prompt("prompt teste")
                .build();

        dto = PromptDto.builder()
                .id(UUID.randomUUID())
                .usuario(usuarioDto)
                .titulo("Teste titulo")
                .prompt("prompt teste")
                .build();
    }

    @Test
    void deveRetornarDomain() {
        Prompt resultado = PromptMapper.paraDomain(dto);

        Assertions.assertEquals(resultado.getId(), dto.getId());
        Assertions.assertEquals(resultado.getUsuario().getId(), dto.getUsuario().getId());
        Assertions.assertEquals(resultado.getTitulo(), dto.getTitulo());
        Assertions.assertEquals(resultado.getPrompt(), dto.getPrompt());
    }

    @Test
    void deveRetornarDto() {
        PromptDto resultado = PromptMapper.paraDto(domain);

        Assertions.assertEquals(resultado.getId(), domain.getId());
        Assertions.assertEquals(resultado.getUsuario().getId(), domain.getUsuario().getId());
        Assertions.assertEquals(resultado.getTitulo(), domain.getTitulo());
        Assertions.assertEquals(resultado.getPrompt(), domain.getPrompt());
    }
}