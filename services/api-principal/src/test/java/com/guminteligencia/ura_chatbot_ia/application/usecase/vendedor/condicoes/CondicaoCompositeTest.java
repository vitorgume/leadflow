package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.condicoes;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.CondicaoLogicaNaoIdentificadoException;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.OperadorLogico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CondicaoCompositeTest {
    // Não usamos @InjectMocks aqui para ter controle manual da ordem da lista
    private CondicaoComposite composite;

    @Mock
    private CondicaoType estrategiaIgual;

    @Mock
    private CondicaoType estrategiaMaiorQue;

    @Test
    @DisplayName("Deve retornar a estratégia correta quando encontrada na lista")
    void deveRetornarEstrategiaQuandoEncontrada() {
        // Arrange
        OperadorLogico operadorAlvo = OperadorLogico.EQUAL; // Ou o enum que você estiver usando

        // Configura os mocks:
        // A estratégia 'MaiorQue' diz NÃO
        when(estrategiaMaiorQue.deveExecutar(operadorAlvo)).thenReturn(false);
        // A estratégia 'Igual' diz SIM
        when(estrategiaIgual.deveExecutar(operadorAlvo)).thenReturn(true);

        // Injeta a lista contendo as duas
        composite = new CondicaoComposite(List.of(estrategiaMaiorQue, estrategiaIgual));

        // Act
        CondicaoType resultado = composite.escolher(operadorAlvo);

        // Assert
        assertNotNull(resultado);
        assertEquals(estrategiaIgual, resultado, "Deveria ter retornado a estratégia que respondeu true");

        // Verifica se o método deveExecutar foi chamado
        verify(estrategiaMaiorQue).deveExecutar(operadorAlvo);
        verify(estrategiaIgual).deveExecutar(operadorAlvo);
    }

    @Test
    @DisplayName("Deve lançar exceção quando nenhuma estratégia aceitar o operador")
    void deveLancarExcecaoQuandoNenhumaAceitar() {
        // Arrange
        OperadorLogico operadorAlvo = OperadorLogico.EQUAL;

        // Ambas dizem NÃO
        when(estrategiaMaiorQue.deveExecutar(operadorAlvo)).thenReturn(false);
        when(estrategiaIgual.deveExecutar(operadorAlvo)).thenReturn(false);

        composite = new CondicaoComposite(List.of(estrategiaMaiorQue, estrategiaIgual));

        // Act & Assert
        assertThrows(CondicaoLogicaNaoIdentificadoException.class,
                () -> composite.escolher(operadorAlvo));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a lista de estratégias estiver vazia")
    void deveLancarExcecaoComListaVazia() {
        // Arrange
        composite = new CondicaoComposite(Collections.emptyList());

        // Act & Assert
        assertThrows(CondicaoLogicaNaoIdentificadoException.class,
                () -> composite.escolher(OperadorLogico.EQUAL));
    }

    @Test
    @DisplayName("Deve retornar a PRIMEIRA estratégia que der match (caso existam duplicadas)")
    void deveRetornarPrimeiraQueDerMatch() {
        // Arrange
        OperadorLogico operadorAlvo = OperadorLogico.EQUAL;

        // Cenário hipotético onde duas estratégias aceitam o mesmo operador
        when(estrategiaIgual.deveExecutar(operadorAlvo)).thenReturn(true);
        // O segundo nem deve ser chamado, mas se fosse, retornaria true também
        // Não mockamos o segundo retorno para garantir que o 'findFirst' parou antes

        composite = new CondicaoComposite(List.of(estrategiaIgual, estrategiaMaiorQue));

        // Act
        CondicaoType resultado = composite.escolher(operadorAlvo);

        // Assert
        assertEquals(estrategiaIgual, resultado);

        // Garante que o fluxo parou no primeiro match (Otimização do stream.findFirst)
        verify(estrategiaIgual).deveExecutar(operadorAlvo);
        verify(estrategiaMaiorQue, never()).deveExecutar(any());
    }

}