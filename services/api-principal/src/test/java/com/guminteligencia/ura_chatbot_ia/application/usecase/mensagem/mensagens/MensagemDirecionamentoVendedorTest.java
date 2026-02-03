package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.UsuarioUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MensagemDirecionamentoVendedorTest {

    @Mock
    private UsuarioUseCase usuarioUseCase; // Injetado mas não usado no método (necessário pro construtor)

    private MensagemDirecionamentoVendedor estrategia;

    @BeforeEach
    void setup() {
        estrategia = new MensagemDirecionamentoVendedor(usuarioUseCase);
    }

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

    @Test
    @DisplayName("Deve substituir todas as variáveis do template corretamente")
    void deveSubstituirVariaveisTemplate() {
        // Arrange
        String template = "Olá {nome_cliente}, vou te passar para o {nome_vendedor}. Seu segmento é {segmento}.";

        usuario.setMensagemDirecionamentoVendedor(template);

        Map<String, Object> atributos = new HashMap<>();
        atributos.put("segmento", "Tecnologia");

        cliente.setNome("Ana");
        cliente.setUsuario(usuario);
        cliente.setAtributosQualificacao(atributos);

        // Act
        String resultado = estrategia.getMensagem("Carlos Vendedor", cliente);

        // Assert
        assertEquals("Olá Ana, vou te passar para o Carlos Vendedor. Seu segmento é Tecnologia.", resultado);
    }

    @Test
    @DisplayName("Deve manter placeholders caso o atributo não exista no cliente")
    void deveManterPlaceholderSeAtributoFaltar() {
        // Arrange
        // Template pede {cidade}, mas cliente não tem esse atributo
        String template = "Cliente: {nome_cliente} de {cidade}.";

        usuario.setMensagemDirecionamentoVendedor(template);

        cliente.setNome("Ana");
        cliente.setUsuario(usuario);
        cliente.setAtributosQualificacao(new HashMap<>()); // Sem atributos

        // Act
        String resultado = estrategia.getMensagem("Vendedor", cliente);

        // Assert
        // {nome_cliente} é substituído, mas {cidade} fica como texto cru
        assertEquals("Cliente: Ana de {cidade}.", resultado);
    }

    @Test
    @DisplayName("Deve funcionar mesmo sem variáveis no template")
    void deveFuncionarSemVariaveis() {
        // Arrange
        String template = "Aguarde um momento.";

        usuario.setMensagemDirecionamentoVendedor(template);

        cliente.setNome("Ana");
        cliente.setUsuario(usuario);
        cliente.setAtributosQualificacao(new HashMap<>());

        // Act
        String resultado = estrategia.getMensagem("Vendedor", cliente);

        // Assert
        assertEquals("Aguarde um momento.", resultado);
    }

    @Test
    @DisplayName("Deve retornar o código correto do TipoMensagem")
    void deveRetornarTipoMensagemCorreto() {
        // Act
        Integer tipo = estrategia.getTipoMensagem();

        // Assert
        assertEquals(TipoMensagem.MENSAGEM_DIRECIONAMENTO_VENDEDOR.getCodigo(), tipo);
    }
}
