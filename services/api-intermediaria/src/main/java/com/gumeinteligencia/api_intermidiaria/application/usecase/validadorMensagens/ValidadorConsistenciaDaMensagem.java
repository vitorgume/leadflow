package com.gumeinteligencia.api_intermidiaria.application.usecase.validadorMensagens;

import com.gumeinteligencia.api_intermidiaria.domain.Mensagem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidadorConsistenciaDaMensagem implements MensagemValidator {

    @Override
    public boolean deveIgnorar(Mensagem mensagem) {
        return isNuloOuVazio(mensagem.getMensagem())
                && isNuloOuVazio(mensagem.getUrlImagem())
                && isNuloOuVazio(mensagem.getUrlAudio())
                && isNuloOuVazio(mensagem.getUrlVideo());
    }

    private boolean isNuloOuVazio(String texto) {
        return texto == null || texto.isBlank();
    }

}
