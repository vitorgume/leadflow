package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConfiguracaoCrmEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.UsuarioEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.VendedorEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

class VendedorMapperTest {

    private Vendedor vendedorDomain;
    private VendedorEntity vendedorEntity;

    @BeforeEach
    void setUp() {
        vendedorDomain = Vendedor.builder()
                .id(1L)
                .nome("Vendedor domain")
                .telefone("0000000000000")
                .inativo(false)
                .idVendedorCrm(123)
                .padrao(false)
                .usuario(
                        Usuario.builder()
                                .id(UUID.randomUUID())
                                .nome("nome teste")
                                .telefone("00000000000")
                                .senha("senhateste123")
                                .email("emailteste@123")
                                .telefoneConcectado("00000000000")
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
                                .build()
                )
                .build();

        vendedorEntity = VendedorEntity.builder()
                .id(2L)
                .nome("Vendedor entity")
                .telefone("0000000000001")
                .inativo(true)
                .idVendedorCrm(123)
                .padrao(false)
                .usuario(
                        UsuarioEntity.builder()
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
                                .build()
                )
                .build();
    }

    @Test
    void deveTransformarParaDomain() {
        Vendedor vendedorTeste = VendedorMapper.paraDomain(vendedorEntity);

        Assertions.assertEquals(vendedorTeste.getId(), vendedorEntity.getId());
        Assertions.assertEquals(vendedorTeste.getNome(), vendedorEntity.getNome());
        Assertions.assertEquals(vendedorTeste.getTelefone(), vendedorEntity.getTelefone());
        Assertions.assertTrue(vendedorTeste.getInativo());
        Assertions.assertFalse(vendedorTeste.getPadrao());
        Assertions.assertEquals(vendedorTeste.getIdVendedorCrm(), vendedorEntity.getIdVendedorCrm());
        Assertions.assertEquals(vendedorTeste.getUsuario().getId(), vendedorEntity.getUsuario().getId());
    }

    @Test
    void deveTransformarParaEntity() {
        VendedorEntity vendedorTeste = VendedorMapper.paraEntity(vendedorDomain);

        Assertions.assertEquals(vendedorTeste.getId(), vendedorDomain.getId());
        Assertions.assertEquals(vendedorTeste.getNome(), vendedorDomain.getNome());
        Assertions.assertEquals(vendedorTeste.getTelefone(), vendedorDomain.getTelefone());
        Assertions.assertFalse(vendedorTeste.getInativo());
        Assertions.assertFalse(vendedorTeste.getPadrao());
        Assertions.assertEquals(vendedorTeste.getIdVendedorCrm(), vendedorDomain.getIdVendedorCrm());
        Assertions.assertEquals(vendedorTeste.getUsuario().getId(), vendedorDomain.getUsuario().getId());
    }
}