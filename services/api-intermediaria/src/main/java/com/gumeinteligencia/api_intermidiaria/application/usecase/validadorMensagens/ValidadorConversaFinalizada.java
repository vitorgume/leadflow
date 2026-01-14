package com.gumeinteligencia.api_intermidiaria.application.usecase.validadorMensagens;

import com.gumeinteligencia.api_intermidiaria.application.usecase.ConversaAgenteUseCase;
import com.gumeinteligencia.api_intermidiaria.domain.ConversaAgente;
import com.gumeinteligencia.api_intermidiaria.domain.Mensagem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidadorConversaFinalizada implements MensagemValidator {

    private final ConversaAgenteUseCase conversaAgenteUseCase;

    @Override
    public boolean deveIgnorar(Mensagem mensagem) {
        return conversaAgenteUseCase.consultarPorTelefoneCliente(mensagem.getTelefone())
                .map(ConversaAgente::getFinalizada)
                .orElse(false);
    }
}
