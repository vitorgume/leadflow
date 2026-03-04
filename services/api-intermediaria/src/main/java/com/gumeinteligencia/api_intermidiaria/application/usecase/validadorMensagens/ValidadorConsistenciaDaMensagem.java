package com.gumeinteligencia.api_intermidiaria.application.usecase.validadorMensagens;

import com.gumeinteligencia.api_intermidiaria.domain.Mensagem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidadorConsistenciaDaMensagem implements MensagemValidator {

    @Override
    public boolean deveIgnorar(Mensagem mensagem) {
        boolean result = isNuloOuVazio(mensagem.getMensagem())
                && isNuloOuVazio(mensagem.getUrlImagem())
                && isNuloOuVazio(mensagem.getUrlAudio())
                && isNuloOuVazio(mensagem.getUrlVideo());

        if(result) {
            log.info("Mensagem ignorada. Motivo: Consistência da mensagem não valida.");
        }

        return result;
    }

    private boolean isNuloOuVazio(String texto) {
        return texto == null || texto.isBlank();
    }

}
