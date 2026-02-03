package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MensagemRecontatoIantivoG1Test {
    private MensagemRecontatoIantivoG1 estrategia;

    private final Usuario usuario = Usuario.builder()
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

    private final Cliente cliente = Cliente.builder()
            .id(UUID.randomUUID())
            .nome("Nome domain")
            .telefone("00000000000")
            .atributosQualificacao(Map.of("teste", new Object()))
            .inativo(false)
            .usuario(usuario)
            .build();

    @BeforeEach
    void setup() {
        estrategia = new MensagemRecontatoIantivoG1();
    }

    @Test
    @DisplayName("Deve substituir nome do cliente e atributos dinâmicos")
    void deveSubstituirVariaveisCorretamente() {
        // Arrange
        String template = "Olá {nome_cliente}! Temos uma oferta de {produto} para você.";

        usuario.setMensagemRecontatoG1(template);

        Map<String, Object> atributos = new HashMap<>();
        atributos.put("produto", "Seguro de Vida");

        cliente.setNome("Maria");
        cliente.setUsuario(usuario);
        cliente.setAtributosQualificacao(atributos);

        // Act
        String resultado = estrategia.getMensagem("Vendedor X", cliente);

        // Assert
        assertEquals("Olá Maria! Temos uma oferta de Seguro de Vida para você.", resultado);
    }

    @Test
    @DisplayName("NÃO deve substituir nome do vendedor (conforme implementação atual)")
    void naoDeveSubstituirNomeVendedor() {
        // Arrange
        // A classe MensagemRecontatoIantivoG1 NÃO tem a linha de replace para {nome_vendedor}
        String template = "Oi {nome_cliente}, sou o {nome_vendedor}.";

        usuario.setMensagemRecontatoG1(template);

        cliente.setNome("João");
        cliente.setUsuario(usuario);
        cliente.setAtributosQualificacao(new HashMap<>());

        // Act
        String resultado = estrategia.getMensagem("Carlos", cliente);

        // Assert
        // O esperado é que {nome_vendedor} continue lá, pois o código não trata ele
        assertEquals("Oi João, sou o {nome_vendedor}.", resultado);
    }

    @Test
    @DisplayName("Deve funcionar mesmo se o cliente não tiver atributos extras")
    void deveFuncionarSemAtributosExtras() {
        // Arrange
        String template = "Olá {nome_cliente}, tudo bem?";

        usuario.setMensagemRecontatoG1(template);

        cliente.setNome("Pedro");
        cliente.setUsuario(usuario);
        cliente.setAtributosQualificacao(new HashMap<>()); // Map vazio

        // Act
        String resultado = estrategia.getMensagem("Vendedor Y", cliente);

        // Assert
        assertEquals("Olá Pedro, tudo bem?", resultado);
    }

    @Test
    @DisplayName("Deve retornar o código correto do TipoMensagem")
    void deveRetornarTipoMensagemCorreto() {
        // Act
        Integer tipo = estrategia.getTipoMensagem();

        // Assert
        assertEquals(TipoMensagem.RECONTATO_INATIVO_G1.getCodigo(), tipo);
    }

}
