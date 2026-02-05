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

class NotEqualCondicaoTest {

    private NotEqualCondicao strategy;

    @BeforeEach
    void setup() {
        strategy = new NotEqualCondicao();
    }

    @Test
    @DisplayName("Executar: Deve retornar TRUE se os valores forem DIFERENTES")
    void deveRetornarTrueParaValoresDiferentes() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("uf", "PR");

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("uf");
        condicao.setValor("SP"); // Diferente de PR

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertTrue(result, "PR é diferente de SP, então deve ser true");
    }

    @Test
    @DisplayName("Executar: Deve retornar FALSE se os valores forem IGUAIS")
    void deveRetornarFalseParaValoresIguais() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("uf", "SP");

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("uf");
        condicao.setValor("SP");

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertFalse(result, "SP não é diferente de SP, então deve ser false");
    }

    @Test
    @DisplayName("Executar: Deve retornar FALSE se valores forem iguais ignorando Case")
    void deveIgnorarCaseSensitive() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("segmento", "varejo"); // Minúsculo

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("segmento");
        condicao.setValor("VAREJO"); // Maiúsculo

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        // Como usa equalsIgnoreCase, eles SÃO iguais.
        // A condição é "Diferente De", logo: (varejo == VAREJO) -> True -> Inverte -> False
        assertFalse(result, "Deve considerar iguais independentemente do case");
    }

    @Test
    @DisplayName("Executar: Deve funcionar com números convertidos para String")
    void deveFuncionarComNumeros() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("idade", 20); // Inteiro

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("idade");
        condicao.setValor("20"); // String

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        // 20 é igual a "20", então "Diferente" é falso
        assertFalse(result);
    }

    @Test
    @DisplayName("Executar: Deve retornar TRUE se número for diferente")
    void deveRetornarTrueComNumerosDiferentes() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("idade", 20);

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("idade");
        condicao.setValor("30");

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Executar: Deve lançar NullPointerException se campo não existir")
    void deveLancarErroSeCampoNaoExistir() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(new HashMap<>()); // Vazio

        Condicao condicao = new Condicao();
        condicao.setCampo("campo_fantasma");
        condicao.setValor("valor");

        // Act & Assert
        // campos.get() retorna null -> null.toString() estoura NPE
        assertThrows(NullPointerException.class,
                () -> strategy.executar(cliente, condicao));
    }

    @Test
    @DisplayName("DeveExecutar: Deve aceitar apenas OperadorLogico.NOT_EQUAL")
    void deveIdentificarOperadorCorreto() {
        assertTrue(strategy.deveExecutar(OperadorLogico.NOT_EQUAL));

        assertFalse(strategy.deveExecutar(OperadorLogico.EQUAL));
        assertFalse(strategy.deveExecutar(OperadorLogico.CONTAINS));
    }

}