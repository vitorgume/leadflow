package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class LimiteDeUmPromptJaAtingidoException extends RuntimeException {

    public LimiteDeUmPromptJaAtingidoException() {
        super("Limite de apenas um prompt atingido.");
    }
}
