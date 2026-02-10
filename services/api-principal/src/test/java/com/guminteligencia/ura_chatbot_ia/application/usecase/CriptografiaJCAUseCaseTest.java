package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.gateways.CriptografiaJCAGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CriptografiaJCAUseCaseTest {

    @Mock
    private CriptografiaJCAGateway gateway;

    @InjectMocks
    private CriptografiaJCAUseCase criptografiaJCAUseCase;

    @Test
    void deveCriptografarComSucesso() {
        String texto = "texto";
        String textoCriptografado = "textoCriptografado";
        Mockito.when(gateway.criptografar(texto)).thenReturn(textoCriptografado);

        String resultado = criptografiaJCAUseCase.criptografar(texto);

        Assertions.assertEquals(textoCriptografado, resultado);
    }

    @Test
    void deveDescriptografarComSucesso() {
        String textoCriptografado = "textoCriptografado";
        String textoDescriptografado = "texto";
        Mockito.when(gateway.descriptografar(textoCriptografado)).thenReturn(textoDescriptografado);

        String resultado = criptografiaJCAUseCase.descriptografar(textoCriptografado);

        Assertions.assertEquals(textoDescriptografado, resultado);
    }
}
