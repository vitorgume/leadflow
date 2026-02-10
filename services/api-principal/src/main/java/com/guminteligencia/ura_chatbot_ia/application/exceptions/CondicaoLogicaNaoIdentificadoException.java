package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class CondicaoLogicaNaoIdentificadoException extends RuntimeException {
    public CondicaoLogicaNaoIdentificadoException() {
        super("Condição lógica não identificada.");
    }
}
