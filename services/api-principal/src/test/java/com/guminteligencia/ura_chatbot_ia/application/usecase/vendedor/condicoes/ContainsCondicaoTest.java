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

class ContainsCondicaoTest {

    private ContainsCondicao strategy;

    @BeforeEach
    void setup() {
        strategy = new ContainsCondicao();
    }

    @Test
    @DisplayName("Executar: Deve retornar TRUE quando o valor conter a string parcial")
    void deveRetornarTrueQuandoContemValor() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("descricao", "Cliente interessado em Varejo e Atacado");

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("descricao");
        condicao.setValor("Varejo"); // "Varejo" está dentro da frase

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Executar: Deve retornar FALSE quando o valor NÃO conter a string")
    void deveRetornarFalseQuandoNaoContemValor() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("segmento", "Indústria");

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("segmento");
        condicao.setValor("Agro"); // Indústria não contém Agro

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Executar: Deve funcionar com números convertidos para String (ex: '123' contem '2')")
    void deveFuncionarComNumeros() {
        // Arrange
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("idade", 12345); // Inteiro no Map

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("idade");
        condicao.setValor("23"); // "12345" contém "23"

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Executar: Deve ser Case Sensitive (Maiúsculas importam)")
    void deveSerCaseSensitive() {
        // Arrange
        // O método .contains() do Java é Case Sensitive por padrão
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("nome", "JOAO");

        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(atributos);

        Condicao condicao = new Condicao();
        condicao.setCampo("nome");
        condicao.setValor("joao"); // minúsculo

        // Act
        boolean result = strategy.executar(cliente, condicao);

        // Assert
        assertFalse(result, "Deveria ser falso pois JOAO != joao");
    }

    @Test
    @DisplayName("Executar: Deve lançar NullPointerException se o campo não existir no cliente")
    void deveLancarErroSeCampoNaoExistir() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setAtributosQualificacao(new HashMap<>()); // Map vazio

        Condicao condicao = new Condicao();
        condicao.setCampo("campo_inexistente");
        condicao.setValor("qualquer");

        // Act & Assert
        // Como o código faz valorCliente.toString(), e valorCliente é null -> NPE
        assertThrows(NullPointerException.class,
                () -> strategy.executar(cliente, condicao));
    }

    @Test
    @DisplayName("DeveExecutar: Deve retornar TRUE apenas para OperadorLogico.CONTAINS")
    void deveIdentificarOperadorCorreto() {
        assertTrue(strategy.deveExecutar(OperadorLogico.CONTAINS));
        assertFalse(strategy.deveExecutar(OperadorLogico.EQUAL));
        assertFalse(strategy.deveExecutar(OperadorLogico.NOT_EQUAL));
    }

}