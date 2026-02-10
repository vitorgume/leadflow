package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class IntegracaoExistenteNaoIdentificada extends RuntimeException {

    public IntegracaoExistenteNaoIdentificada() {
        super("Integração existente não identificada.");
    }
}
