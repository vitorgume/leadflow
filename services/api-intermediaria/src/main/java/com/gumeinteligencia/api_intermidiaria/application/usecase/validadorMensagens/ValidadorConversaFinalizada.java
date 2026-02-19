package com.gumeinteligencia.api_intermidiaria.application.usecase.validadorMensagens;

import com.gumeinteligencia.api_intermidiaria.application.usecase.ConversaAgenteUseCase;
import com.gumeinteligencia.api_intermidiaria.domain.ConversaAgente;
import com.gumeinteligencia.api_intermidiaria.domain.Mensagem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidadorConversaFinalizada implements MensagemValidator {

    private final ConversaAgenteUseCase conversaAgenteUseCase;

    @Override
    public boolean deveIgnorar(Mensagem mensagem) {
        boolean result = conversaAgenteUseCase.consultarPorTelefoneCliente(mensagem.getTelefone())
                .map(ConversaAgente::getFinalizada)
                .orElse(false);

        if(result) {
            log.info("Mensagem ignorada. Motivo: Conversa já finalizada");
        }

        return result;
    }
}
