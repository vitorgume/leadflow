package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
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
class MensagemRedirecionamentoRecontatoTest {

    private MensagemRedirecionamentoRecontato estrategia;
    private Usuario usuario;
    private Cliente cliente;

    @BeforeEach
    void setup() {
        estrategia = new MensagemRedirecionamentoRecontato();

        // Criando objetos base limpos para serem reutilizados/modificados nos testes
        usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("Usuario Teste")
                .mensagemEncaminhamento("Template base") // Atributo alvo desta estratégia
                .build();

        cliente = Cliente.builder()
                .id(UUID.randomUUID())
                .nome("Cliente Teste")
                .usuario(usuario)
                .atributosQualificacao(new HashMap<>())
                .build();
    }

    @Test
    @DisplayName("Deve substituir todas as variáveis do template corretamente")
    void deveSubstituirVariaveisTemplate() {
        // Arrange
        String template = "Olá {nome_cliente}, vamos te redirecionar para o vendedor {nome_vendedor}. O seu protocolo de atendimento é {protocolo}.";

        usuario.setMensagemEncaminhamento(template);

        Map<String, Object> atributos = new HashMap<>();
        atributos.put("protocolo", "12345-X");

        cliente.setNome("João");
        cliente.setAtributosQualificacao(atributos);

        // Act
        String resultado = estrategia.getMensagem("Carlos Vendedor", cliente);

        // Assert
        assertEquals("Olá João, vamos te redirecionar para o vendedor Carlos Vendedor. O seu protocolo de atendimento é 12345-X.", resultado);
    }

    @Test
    @DisplayName("Deve manter placeholders caso o atributo não exista no cliente")
    void deveManterPlaceholderSeAtributoFaltar() {
        // Arrange
        // Template pede {cidade}, mas o cliente não tem esse atributo
        String template = "Cliente: {nome_cliente}. Redirecionando para a filial de {cidade}.";

        usuario.setMensagemEncaminhamento(template);

        cliente.setNome("Maria");
        cliente.setAtributosQualificacao(new HashMap<>()); // Sem atributos extras

        // Act
        String resultado = estrategia.getMensagem("Ana Vendedora", cliente);

        // Assert
        // {nome_cliente} é substituído, mas {cidade} fica como texto cru
        assertEquals("Cliente: Maria. Redirecionando para a filial de {cidade}.", resultado);
    }

    @Test
    @DisplayName("Deve funcionar mesmo sem variáveis no template")
    void deveFuncionarSemVariaveis() {
        // Arrange
        String template = "Aguarde, estamos redirecionando para nossas atendentes.";

        usuario.setMensagemEncaminhamento(template);

        cliente.setNome("Maria");
        cliente.setAtributosQualificacao(new HashMap<>());

        // Act
        String resultado = estrategia.getMensagem("Ana Vendedora", cliente);

        // Assert
        assertEquals("Aguarde, estamos redirecionando para nossas atendentes.", resultado);
    }

    @Test
    @DisplayName("Deve retornar o código correto do TipoMensagem")
    void deveRetornarTipoMensagemCorreto() {
        // Act
        Integer tipo = estrategia.getTipoMensagem();

        // Assert
        assertEquals(TipoMensagem.REDIRECIONAMENTO_RECONTATO.getCodigo(), tipo);
    }
}
