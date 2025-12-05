package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.gateways.MensageriaGateway;
import com.guminteligencia.ura_chatbot_ia.domain.AvisoContexto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MensageriaUseCaseTest {

    @Mock
    private MensageriaGateway mensageriaGateway;

    @InjectMocks
    private MensageriaUseCase useCase;

    @Mock
    private Message mensagemFila;

    @Test
    void deveListarAvisosDelegandoParaGateway() {
        AvisoContexto aviso = mock(AvisoContexto.class);
        List<AvisoContexto> lista = List.of(aviso);
        when(mensageriaGateway.listarAvisos()).thenReturn(lista);

        List<AvisoContexto> result = useCase.listarAvisos();

        assertSame(lista, result, "Deve retornar exatamente a lista fornecida pelo gateway");
        verify(mensageriaGateway).listarAvisos();
        verifyNoMoreInteractions(mensageriaGateway);
    }

    @Test
    void deveDeletarMensagemDelegandoParaGateway() {
        useCase.deletarMensagem(mensagemFila);

        verify(mensageriaGateway).deletarMensagem(mensagemFila);
        verifyNoMoreInteractions(mensageriaGateway);
    }
}
