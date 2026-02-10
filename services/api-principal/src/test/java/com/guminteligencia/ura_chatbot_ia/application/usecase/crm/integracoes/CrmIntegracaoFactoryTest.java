package com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.IntegracaoExistenteNaoIdentificada;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrmIntegracaoFactoryTest {

    private CrmIntegracaoFactory factory;

    @Mock
    private CrmIntegracaoType integracaoKommo;

    @Test
    @DisplayName("Deve retornar a implementação quando solicitada (Cenário Sucesso)")
    void deveRetornarIntegracaoKommo() {
        // Arrange
        // A implementação diz: "Eu sou do tipo KOMMO"
        when(integracaoKommo.getCrmType()).thenReturn(CrmType.KOMMO);

        // A fábrica conhece essa implementação
        factory = new CrmIntegracaoFactory(List.of(integracaoKommo));

        // Act
        CrmIntegracaoType result = factory.create(CrmType.KOMMO);

        // Assert
        assertNotNull(result);
        assertEquals(integracaoKommo, result);
    }

    @Test
    @DisplayName("Deve lançar exceção se a lista de integrações estiver vazia")
    void deveLancarExcecaoComListaVazia() {
        // Arrange
        // Cenário onde o Spring não injetou nenhuma implementação
        factory = new CrmIntegracaoFactory(Collections.emptyList());

        // Act & Assert
        assertThrows(IntegracaoExistenteNaoIdentificada.class,
                () -> factory.create(CrmType.KOMMO));
    }

    @Test
    @DisplayName("Deve lançar exceção se passar NULL (Segurança)")
    void deveLancarExcecaoSeInputForNull() {
        // Arrange
        // Mesmo que a implementação exista...
        when(integracaoKommo.getCrmType()).thenReturn(CrmType.KOMMO);
        factory = new CrmIntegracaoFactory(List.of(integracaoKommo));

        // Act & Assert
        // ...se eu pedir null, o equals() vai dar false ou o filter não vai achar nada
        // e deve cair no orElseThrow
        assertThrows(IntegracaoExistenteNaoIdentificada.class,
                () -> factory.create(null));
    }
}