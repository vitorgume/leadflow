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

class IsGreaterThanCondicaoTest {

    private IsGreaterThanCondicao strategy;

    @BeforeEach
    void setup() {
        strategy = new IsGreaterThanCondicao();
    }

    @Test
    @DisplayName("Executar: Deve retornar TRUE se valor do cliente for MAIOR que o da condição")
    void deveRetornarTrueQuandoMaior() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("score", 800);

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("score");
        condicao.setValor("500");

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertTrue(result, "800 > 500 deveria ser true");
    }

    @Test
    @DisplayName("Executar: Deve retornar FALSE se valor do cliente for MENOR")
    void deveRetornarFalseQuandoMenor() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("idade", 15);

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("idade");
        condicao.setValor("18");

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertFalse(result, "15 > 18 deveria ser false");
    }

    @Test
    @DisplayName("Executar: Deve retornar FALSE se valor for IGUAL (Pois é estritamente maior)")
    void deveRetornarFalseQuandoIgual() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("pontos", 100);

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("pontos");
        condicao.setValor("100");

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertFalse(result, "100 > 100 deveria ser false");
    }

    @Test
    @DisplayName("Executar: Deve funcionar se o valor do cliente for uma String numérica")
    void deveFuncionarComStringNumerica() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("idade", "30"); // String

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("idade");
        condicao.setValor("25");

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertTrue(result, "30 > 25 deveria ser true");
    }

    @Test
    @DisplayName("Executar: Deve retornar FALSE e logar erro se valor do cliente não for número")
    void deveRetornarFalseSeClienteNaoForNumero() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("idade", "trinta"); // Inválido para int

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("idade");
        condicao.setValor("18");

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertFalse(result, "Deveria retornar false ao cair no catch de NumberFormatException");
    }

    @Test
    @DisplayName("Executar: Deve retornar FALSE e logar erro se valor da condição não for número")
    void deveRetornarFalseSeCondicaoNaoForNumero() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("idade", 20);

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("idade");
        condicao.setValor("dezoito"); // Inválido

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Executar: Deve lançar NullPointerException se campo não existir no cliente")
    void deveLancarErroSeCampoNaoExistir() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(new HashMap<>()); // Vazio

        Condicao condicao = new Condicao();
        condicao.setCampo("idade");
        condicao.setValor("18");

        // Act & Assert
        // O código tenta fazer null.toString() antes do try/catch
        assertThrows(NullPointerException.class,
                () -> strategy.executar(cliente, condicao));
    }

    @Test
    @DisplayName("DeveExecutar: Deve aceitar apenas OperadorLogico.IS_GREATER_THAN")
    void deveIdentificarOperadorCorreto() {
        assertTrue(strategy.deveExecutar(OperadorLogico.IS_GREATER_THAN));

        assertFalse(strategy.deveExecutar(OperadorLogico.IS_LESS_THAN));
        assertFalse(strategy.deveExecutar(OperadorLogico.EQUAL));
    }

}