package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.condicoes;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Condicao;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.OperadorLogico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class IsGreaterThanOrEqualToCondicaoTest {

    private IsGreaterThanOrEqualToCondicao strategy;

    @BeforeEach
    void setup() {
        strategy = new IsGreaterThanOrEqualToCondicao();
    }

    @Test
    @DisplayName("Executar: Deve retornar TRUE se valor do cliente for MAIOR")
    void deveRetornarTrueQuandoMaior() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("idade", 20);

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("idade");
        condicao.setValor("18");

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertTrue(result, "20 >= 18 deve ser true");
    }

    @Test
    @DisplayName("Executar: Deve retornar TRUE se valor do cliente for IGUAL")
    void deveRetornarTrueQuandoIgual() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("idade", 18);

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("idade");
        condicao.setValor("18");

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertTrue(result, "18 >= 18 deve ser true");
    }

    @Test
    @DisplayName("Executar: Deve retornar FALSE se valor do cliente for MENOR")
    void deveRetornarFalseQuandoMenor() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("score", 400);

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("score");
        condicao.setValor("500");

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertFalse(result, "400 >= 500 deve ser false");
    }

    @Test
    @DisplayName("Executar: Deve funcionar corretamente com Strings numéricas")
    void deveFuncionarComString() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("nivel", "5"); // String

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("nivel");
        condicao.setValor("5");

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Executar: Deve retornar FALSE e logar erro se Cliente tiver valor inválido")
    void deveRetornarFalseSeClienteNaoForNumero() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("idade", "abc"); // Não numérico

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("idade");
        condicao.setValor("18");

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertFalse(result, "Deve cair no catch de NumberFormatException");
    }

    @Test
    @DisplayName("Executar: Deve retornar FALSE e logar erro se Condição tiver valor inválido")
    void deveRetornarFalseSeCondicaoNaoForNumero() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("idade", 18);

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("idade");
        condicao.setValor("dezoito"); // Não numérico

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Executar: Deve lançar NullPointerException se campo não existir")
    void deveLancarErroSeCampoInexistente() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(new HashMap<>()); // Mapa vazio

        Condicao condicao = new Condicao();
        condicao.setCampo("idade");
        condicao.setValor("18");

        // Act & Assert
        // campos.get("idade") retorna null -> null.toString() estoura NPE
        assertThrows(NullPointerException.class,
                () -> strategy.executar(cliente, condicao));
    }

    @Test
    @DisplayName("DeveExecutar: Deve aceitar apenas OperadorLogico.IS_GREATER_THAN_OR_EQUAL_TO")
    void deveIdentificarOperadorCorreto() {
        assertTrue(strategy.deveExecutar(OperadorLogico.IS_GREATER_THAN_OR_EQUAL_TO));

        assertFalse(strategy.deveExecutar(OperadorLogico.IS_GREATER_THAN));
        assertFalse(strategy.deveExecutar(OperadorLogico.EQUAL));
    }

}