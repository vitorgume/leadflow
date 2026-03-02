package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class IntegracaoExistenteNaoIdentificadaException extends RuntimeException {

    public IntegracaoExistenteNaoIdentificadaException() {
        super("Integração existente não identificada.");
    }
}
