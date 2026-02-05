package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.*;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConfiguracaoCrmEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.UsuarioEntity;
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

import static org.junit.jupiter.api.Assertions.*;

class ConfiguracaoEscolhaVendedorMapperTest {

    private ConfiguracaoEscolhaVendedor domain;
    private ConfiguracaoEscolhaVendedorEntity entity;

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

        ConfiguracaoCrmEntity configCrmEntity = ConfiguracaoCrmEntity.builder()
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

        UsuarioEntity usuarioEntity = UsuarioEntity.builder()
                .id(usuarioDomain.getId())
                .nome("Usuario Teste Completo")
                .telefone("5511999999999")
                .senha("senhaForte123")
                .email("usuario@teste.com")
                .telefoneConectado("5511988888888")
                .atributosQualificacao(Map.of("perfil", "admin", "score", 100))
                .configuracaoCrm(configCrmEntity)
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

        VendedorEntity vendedorEntity = VendedorEntity.builder()
                .id(10L)
                .nome("Vendedor Top")
                .telefone("5511977777777")
                .inativo(false)
                .idVendedorCrm(999)
                .padrao(true)
                .usuario(usuarioEntity)
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

        CondicaoEntity condicaoEntity = CondicaoEntity.builder()
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

        entity = ConfiguracaoEscolhaVendedorEntity.builder()
                .id(domain.getId())
                .prioridade(1)
                .usuario(usuarioEntity)
                .vendedores(List.of(vendedorEntity))
                .condicoes(List.of(condicaoEntity))
                .build();
    }

    @Test
    @DisplayName("Deve transformar Entity para Domain corretamente (Todos os campos)")
    void deveTransformarParaDomain() {
        // Act
        ConfiguracaoEscolhaVendedor resultado = ConfiguracaoEscolhaVendedorMapper.paraDomain(entity);

        // Assert - Configuração Principal
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(entity.getId(), resultado.getId());
        Assertions.assertEquals(entity.getPrioridade(), resultado.getPrioridade());

        // Assert - Usuário (Verificação profunda)
        Usuario usuarioRes = resultado.getUsuario();
        Assertions.assertNotNull(usuarioRes);
        Assertions.assertEquals(entity.getUsuario().getId(), usuarioRes.getId());
        Assertions.assertEquals(entity.getUsuario().getNome(), usuarioRes.getNome());
        Assertions.assertEquals(entity.getUsuario().getEmail(), usuarioRes.getEmail());
        Assertions.assertEquals(entity.getUsuario().getConfiguracaoCrm().getCrmType(), usuarioRes.getConfiguracaoCrm().getCrmType());

        // Assert - Vendedores
        Assertions.assertNotNull(resultado.getVendedores());
        Assertions.assertEquals(1, resultado.getVendedores().size());
        Vendedor vendedorRes = resultado.getVendedores().get(0);
        Assertions.assertEquals(entity.getVendedores().get(0).getId(), vendedorRes.getId());
        Assertions.assertEquals(entity.getVendedores().get(0).getNome(), vendedorRes.getNome());
        Assertions.assertEquals(entity.getVendedores().get(0).getIdVendedorCrm(), vendedorRes.getIdVendedorCrm());
        Assertions.assertEquals(entity.getVendedores().get(0).getPadrao(), vendedorRes.getPadrao());

        // Assert - Condições
        Assertions.assertNotNull(resultado.getCondicoes());
        Assertions.assertEquals(1, resultado.getCondicoes().size());
        Condicao condicaoRes = resultado.getCondicoes().get(0);
        Assertions.assertEquals(entity.getCondicoes().get(0).getId(), condicaoRes.getId());
        Assertions.assertEquals(entity.getCondicoes().get(0).getCampo(), condicaoRes.getCampo());
        Assertions.assertEquals(entity.getCondicoes().get(0).getValor(), condicaoRes.getValor());
    }

    @Test
    @DisplayName("Deve transformar Domain para Entity corretamente (Todos os campos)")
    void deveTransformarParaEntity() {
        // Act
        ConfiguracaoEscolhaVendedorEntity resultado = ConfiguracaoEscolhaVendedorMapper.paraEntity(domain);

        // Assert - Configuração Principal
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(domain.getId(), resultado.getId());
        Assertions.assertEquals(domain.getPrioridade(), resultado.getPrioridade());

        // Assert - Usuário
        Assertions.assertNotNull(resultado.getUsuario());
        Assertions.assertEquals(domain.getUsuario().getId(), resultado.getUsuario().getId());
        Assertions.assertEquals(domain.getUsuario().getTelefone(), resultado.getUsuario().getTelefone());
        Assertions.assertEquals(domain.getUsuario().getWhatsappToken(), resultado.getUsuario().getWhatsappToken());

        // Assert - Vendedores
        Assertions.assertNotNull(resultado.getVendedores());
        Assertions.assertEquals(1, resultado.getVendedores().size());
        VendedorEntity vendedorRes = resultado.getVendedores().get(0);
        Assertions.assertEquals(domain.getVendedores().get(0).getId(), vendedorRes.getId());
        Assertions.assertEquals(domain.getVendedores().get(0).getNome(), vendedorRes.getNome());
        Assertions.assertEquals(domain.getVendedores().get(0).getInativo(), vendedorRes.getInativo());

        // Assert - Condições
        Assertions.assertNotNull(resultado.getCondicoes());
        Assertions.assertEquals(1, resultado.getCondicoes().size());
        CondicaoEntity condicaoRes = resultado.getCondicoes().get(0);
        Assertions.assertEquals(domain.getCondicoes().get(0).getId(), condicaoRes.getId());
        Assertions.assertEquals(domain.getCondicoes().get(0).getOperadorLogico(), condicaoRes.getOperadorLogico());
        Assertions.assertEquals(domain.getCondicoes().get(0).getConectorLogico(), condicaoRes.getConectorLogico());
    }

}