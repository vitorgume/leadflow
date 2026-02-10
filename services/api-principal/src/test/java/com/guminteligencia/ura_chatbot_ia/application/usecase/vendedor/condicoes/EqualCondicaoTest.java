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

class EqualCondicaoTest {

    private EqualCondicao strategy;

    @BeforeEach
    void setup() {
        strategy = new EqualCondicao();
    }

    @Test
    @DisplayName("Executar: Deve retornar TRUE para strings idênticas")
    void deveRetornarTrueParaStringsIguais() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("uf", "PR");

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("uf");
        condicao.setValor("PR");

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Executar: Deve retornar TRUE ignorando Case (Maiúsculas/Minúsculas)")
    void deveIgnorarCaseSensitive() {
        // Arrange
        // No cliente está minúsculo "pr", na condição está maiúsculo "PR"
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("uf", "pr");

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("uf");
        condicao.setValor("PR");

        // Act
        // Deve retornar true pois usa equalsIgnoreCase
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Executar: Deve retornar FALSE para valores diferentes")
    void deveRetornarFalseParaValoresDiferentes() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("uf", "SP");

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("uf");
        condicao.setValor("PR");

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Executar: Deve funcionar convertendo números para String (ex: 10 == '10')")
    void deveFuncionarComNumeros() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("quantidade", 10); // Inteiro

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("quantidade");
        condicao.setValor("10"); // String

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Executar: Deve lançar NullPointerException se o campo não existir")
    void deveLancarErroSeCampoNaoExistir() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(new HashMap<>()); // Vazio

        Condicao condicao = new Condicao();
        condicao.setCampo("campo_fantasma");
        condicao.setValor("valor");

        // Act & Assert
        // O código tenta fazer null.toString()
        assertThrows(NullPointerException.class,
                () -> strategy.executar(cliente, condicao));
    }

    @Test
    @DisplayName("DeveExecutar: Deve aceitar apenas OperadorLogico.EQUAL")
    void deveIdentificarOperadorCorreto() {
        assertTrue(strategy.deveExecutar(OperadorLogico.EQUAL));

        assertFalse(strategy.deveExecutar(OperadorLogico.NOT_EQUAL));
        assertFalse(strategy.deveExecutar(OperadorLogico.CONTAINS));
    }

}