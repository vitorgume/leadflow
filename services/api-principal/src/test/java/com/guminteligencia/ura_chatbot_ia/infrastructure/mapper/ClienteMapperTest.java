package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ClienteEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConfiguracaoCrmEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.UsuarioEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

class ClienteMapperTest {

    private Cliente clienteDomain;
    private ClienteEntity clienteEntity;
    private Usuario usuarioDomain;
    private UsuarioEntity usuarioEntity;

    @BeforeEach
    void setUp() {
        usuarioDomain = Usuario.builder()
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
                .build();

        usuarioEntity = UsuarioEntity.builder()
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

        clienteDomain = Cliente.builder()
                .id(UUID.randomUUID())
                .nome("Nome domain")
                .telefone("00000000000")
                .atributosQualificacao(Map.of("teste", new Object()))
                .inativo(false)
                .usuario(usuarioDomain)
                .build();

        clienteEntity = ClienteEntity.builder()
                .id(UUID.randomUUID())
                .nome("Nome entity")
                .telefone("000000000001")
                .atributosQualificacao(Map.of("teste", new Object()))
                .inativo(true)
                .usuario(usuarioEntity)
                .build();
    }

    @Test
    void deveTransformarParaDomain() {
        Cliente clienteTeste = ClienteMapper.paraDomain(clienteEntity);

        Assertions.assertEquals(clienteTeste.getId(), clienteEntity.getId());
        Assertions.assertEquals(clienteTeste.getNome(), clienteEntity.getNome());
        Assertions.assertEquals(clienteTeste.getTelefone(), clienteEntity.getTelefone());
        Assertions.assertEquals(clienteTeste.getAtributosQualificacao(), clienteEntity.getAtributosQualificacao());
        Assertions.assertEquals(clienteTeste.getUsuario().getId(), clienteEntity.getUsuario().getId());
        Assertions.assertTrue(clienteTeste.isInativo());
    }

    @Test
    void deveTransformarParaEntity() {
        ClienteEntity clienteTeste = ClienteMapper.paraEntity(clienteDomain);

        Assertions.assertEquals(clienteTeste.getId(), clienteDomain.getId());
        Assertions.assertEquals(clienteTeste.getNome(), clienteDomain.getNome());
        Assertions.assertEquals(clienteTeste.getTelefone(), clienteDomain.getTelefone());
        Assertions.assertEquals(clienteTeste.getUsuario().getId(), clienteDomain.getUsuario().getId());
        Assertions.assertFalse(clienteTeste.isInativo());
    }
}