package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.*;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ConfiguracaoCrmDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.UsuarioDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.vendedor.CondicaoDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.vendedor.ConfiguracaoEscolhaVendedorDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.vendedor.VendedorDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.CondicaoEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.ConfiguracaoEscolhaVendedorEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.VendedorEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

class ConfiguracaoEscolhaVendedorMapperTest {

    private ConfiguracaoEscolhaVendedor domain;
    private ConfiguracaoEscolhaVendedorDto dto;

    @BeforeEach
    void setUp() {
        // ==================================================================================
        // 1. SETUP DE USUÁRIO (Completo com Configuração CRM e Atributos)
        // ==================================================================================
        ConfiguracaoCrm configCrmDomain = ConfiguracaoCrm.builder()
                .crmType(CrmType.KOMMO)
                .mapeamentoCampos(Map.of("campo1", "valor1"))
                .idTagAtivo("tag-ativo-123")
                .idEtapaAtivos("etapa-ativos-123")
                .idEtapaInativos("etapa-inativos-123")
                .acessToken("token-crm-secret")
                .build();

        ConfiguracaoCrmDto configuracaoCrmDto = ConfiguracaoCrmDto.builder()
                .crmType(CrmType.KOMMO)
                .mapeamentoCampos(Map.of("campo1", "valor1"))
                .idTagAtivo("tag-ativo-123")
                .idEtapaAtivos("etapa-ativos-123")
                .idEtapaInativos("etapa-inativos-123")
                .acessToken("token-crm-secret")
                .build();

        Usuario usuarioDomain = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("Usuario Teste Completo")
                .telefone("5511999999999")
                .senha("senhaForte123")
                .email("usuario@teste.com")
                .telefoneConectado("5511988888888")
                .atributosQualificacao(Map.of("perfil", "admin", "score", 100))
                .configuracaoCrm(configCrmDomain)
                .mensagemDirecionamentoVendedor("Olá, vou te transferir.")
                .mensagemRecontatoG1("Olá novamente.")
                .whatsappToken("wpp-token-xyz")
                .whatsappIdInstance("wpp-instance-01")
                .agenteApiKey("agent-api-key-abc")
                .build();

        UsuarioDto usuarioDto = UsuarioDto.builder()
                .id(usuarioDomain.getId())
                .nome("Usuario Teste Completo")
                .telefone("5511999999999")
                .senha("senhaForte123")
                .email("usuario@teste.com")
                .telefoneConectado("5511988888888")
                .atributosQualificacao(Map.of("perfil", "admin", "score", 100))
                .configuracaoCrm(configuracaoCrmDto)
                .mensagemDirecionamentoVendedor("Olá, vou te transferir.")
                .mensagemRecontatoG1("Olá novamente.")
                .whatsappToken("wpp-token-xyz")
                .whatsappIdInstance("wpp-instance-01")
                .agenteApiKey("agent-api-key-abc")
                .build();

        // ==================================================================================
        // 2. SETUP DE VENDEDOR (Completo)
        // ==================================================================================
        Vendedor vendedorDomain = Vendedor.builder()
                .id(10L)
                .nome("Vendedor Top")
                .telefone("5511977777777")
                .inativo(false)
                .idVendedorCrm(999)
                .padrao(true)
                .usuario(usuarioDomain) // Vínculo com usuário
                .build();

        VendedorDto vendedorDto = VendedorDto.builder()
                .id(10L)
                .nome("Vendedor Top")
                .telefone("5511977777777")
                .inativo(false)
                .idVendedorCrm(999)
                .padrao(true)
                .usuario(usuarioDto)
                .build();

        // ==================================================================================
        // 3. SETUP DE CONDIÇÃO (Completo)
        // ==================================================================================
        Condicao condicaoDomain = Condicao.builder()
                .id(UUID.randomUUID())
                .campo("faturamento")
                .operadorLogico(OperadorLogico.IS_GREATER_THAN)
                .valor("50000")
                .conectorLogico(ConectorLogico.AND)
                .build();

        CondicaoDto condicaoDto = CondicaoDto.builder()
                .id(condicaoDomain.getId())
                .campo("faturamento")
                .operadorLogico(OperadorLogico.IS_GREATER_THAN)
                .valor("50000")
                .conectorLogico(ConectorLogico.AND)
                .build();

        // ==================================================================================
        // 4. SETUP DO OBJETO PRINCIPAL (ConfiguracaoEscolhaVendedor)
        // ==================================================================================
        domain = ConfiguracaoEscolhaVendedor.builder()
                .id(UUID.randomUUID())
                .prioridade(1)
                .usuario(usuarioDomain)
                .vendedores(List.of(vendedorDomain))
                .condicoes(List.of(condicaoDomain))
                .build();

        dto = ConfiguracaoEscolhaVendedorDto.builder()
                .id(domain.getId())
                .prioridade(1)
                .usuario(usuarioDto)
                .vendedores(List.of(vendedorDto))
                .condicoes(List.of(condicaoDto))
                .build();
    }

    @Test
    @DisplayName("Deve transformar Entity para Domain corretamente (Todos os campos)")
    void deveTransformarParaDomain() {
        // Act
        ConfiguracaoEscolhaVendedor resultado = ConfiguracaoEscolhaVendedorMapper.paraDomain(dto);

        // Assert - Configuração Principal
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(dto.getId(), resultado.getId());
        Assertions.assertEquals(dto.getPrioridade(), resultado.getPrioridade());

        // Assert - Usuário (Verificação profunda)
        Usuario usuarioRes = resultado.getUsuario();
        Assertions.assertNotNull(usuarioRes);
        Assertions.assertEquals(dto.getUsuario().getId(), usuarioRes.getId());

        // Assert - Vendedores
        Assertions.assertNotNull(resultado.getVendedores());
        Assertions.assertEquals(1, resultado.getVendedores().size());
        Vendedor vendedorRes = resultado.getVendedores().get(0);
        Assertions.assertEquals(dto.getVendedores().get(0).getId(), vendedorRes.getId());

        // Assert - Condições
        Assertions.assertNotNull(resultado.getCondicoes());
        Assertions.assertEquals(1, resultado.getCondicoes().size());
        Condicao condicaoRes = resultado.getCondicoes().get(0);
        Assertions.assertEquals(dto.getCondicoes().get(0).getId(), condicaoRes.getId());
        Assertions.assertEquals(dto.getCondicoes().get(0).getCampo(), condicaoRes.getCampo());
        Assertions.assertEquals(dto.getCondicoes().get(0).getValor(), condicaoRes.getValor());
    }

    @Test
    @DisplayName("Deve transformar Domain para Entity corretamente (Todos os campos)")
    void deveTransformarParaEntity() {
        // Act
        ConfiguracaoEscolhaVendedorDto resultado = ConfiguracaoEscolhaVendedorMapper.paraDto(domain);

        // Assert - Configuração Principal
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(domain.getId(), resultado.getId());
        Assertions.assertEquals(domain.getPrioridade(), resultado.getPrioridade());

        // Assert - Usuário
        Assertions.assertNotNull(resultado.getUsuario());
        Assertions.assertEquals(domain.getUsuario().getId(), resultado.getUsuario().getId());
        Assertions.assertEquals(domain.getUsuario().getTelefone(), resultado.getUsuario().getTelefone());
        Assertions.assertNull(resultado.getUsuario().getWhatsappToken());

        // Assert - Vendedores
        Assertions.assertNotNull(resultado.getVendedores());
        Assertions.assertEquals(1, resultado.getVendedores().size());
        VendedorDto vendedorRes = resultado.getVendedores().get(0);
        Assertions.assertEquals(domain.getVendedores().get(0).getId(), vendedorRes.getId());
        Assertions.assertEquals(domain.getVendedores().get(0).getNome(), vendedorRes.getNome());
        Assertions.assertEquals(domain.getVendedores().get(0).getInativo(), vendedorRes.getInativo());

        // Assert - Condições
        Assertions.assertNotNull(resultado.getCondicoes());
        Assertions.assertEquals(1, resultado.getCondicoes().size());
        CondicaoDto condicaoRes = resultado.getCondicoes().get(0);
        Assertions.assertEquals(domain.getCondicoes().get(0).getId(), condicaoRes.getId());
        Assertions.assertEquals(domain.getCondicoes().get(0).getOperadorLogico(), condicaoRes.getOperadorLogico());
        Assertions.assertEquals(domain.getCondicoes().get(0).getConectorLogico(), condicaoRes.getConectorLogico());
    }

}