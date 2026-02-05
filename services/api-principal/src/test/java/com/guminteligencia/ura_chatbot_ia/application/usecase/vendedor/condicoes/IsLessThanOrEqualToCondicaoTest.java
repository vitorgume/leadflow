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

class IsLessThanOrEqualToCondicaoTest {

    private IsLessThanOrEqualToCondicao strategy;

    @BeforeEach
    void setup() {
        strategy = new IsLessThanOrEqualToCondicao();
    }

    @Test
    @DisplayName("Executar: Deve retornar TRUE se valor do cliente for MENOR")
    void deveRetornarTrueQuandoMenor() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("preco", 40);

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("preco");
        condicao.setValor("50");

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertTrue(result, "40 <= 50 deve ser true");
    }

    @Test
    @DisplayName("Executar: Deve retornar TRUE se valor do cliente for IGUAL")
    void deveRetornarTrueQuandoIgual() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("limite", 100);

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("limite");
        condicao.setValor("100");

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertTrue(result, "100 <= 100 deve ser true (Inclusivo)");
    }

    @Test
    @DisplayName("Executar: Deve retornar FALSE se valor do cliente for MAIOR")
    void deveRetornarFalseQuandoMaior() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("idade", 21);

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("idade");
        condicao.setValor("18");

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertFalse(result, "21 <= 18 deve ser false");
    }

    @Test
    @DisplayName("Executar: Deve funcionar com String numérica no cliente")
    void deveFuncionarComString() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("nivel", "1"); // String

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("nivel");
        condicao.setValor("5");

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertTrue(result, "1 <= 5 deve ser true");
    }

    @Test
    @DisplayName("Executar: Deve retornar FALSE e logar erro se Cliente tiver valor inválido")
    void deveRetornarFalseSeClienteInvalido() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("tamanho", "grande"); // Não converte para int

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("tamanho");
        condicao.setValor("10");

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertFalse(result, "Deve cair no catch de NumberFormatException");
    }

    @Test
    @DisplayName("Executar: Deve retornar FALSE e logar erro se Condição tiver valor inválido")
    void deveRetornarFalseSeCondicaoInvalida() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("tamanho", 10);

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("tamanho");
        condicao.setValor("dez"); // Inválido

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Executar: Deve lançar NullPointerException se campo não existir")
    void deveLancarErroSeCampoNaoExistir() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(new HashMap<>()); // Vazio

        Condicao condicao = new Condicao();
        condicao.setCampo("campo_inexistente");
        condicao.setValor("10");

        // Act & Assert
        // get() retorna null -> null.toString() gera NPE
        assertThrows(NullPointerException.class,
                () -> strategy.executar(cliente, condicao));
    }

    @Test
    @DisplayName("DeveExecutar: Deve aceitar apenas OperadorLogico.IS_LESS_THAN_OR_EQUAL_TO")
    void deveIdentificarOperadorCorreto() {
        assertTrue(strategy.deveExecutar(OperadorLogico.IS_LESS_THAN_OR_EQUAL_TO));

        assertFalse(strategy.deveExecutar(OperadorLogico.IS_LESS_THAN));
        assertFalse(strategy.deveExecutar(OperadorLogico.EQUAL));
    }

}