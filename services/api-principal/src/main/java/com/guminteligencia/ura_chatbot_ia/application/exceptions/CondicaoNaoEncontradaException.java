package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class CondicaoNaoEncontradaException extends RuntimeException {

    public CondicaoNaoEncontradaException() {
        super("Condição não encontrada.");
    }
}
