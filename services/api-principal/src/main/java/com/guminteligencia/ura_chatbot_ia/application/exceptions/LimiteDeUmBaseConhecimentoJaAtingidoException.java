package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class LimiteDeUmBaseConhecimentoJaAtingidoException extends RuntimeException {
    public LimiteDeUmBaseConhecimentoJaAtingidoException() {
        super("Limite de uma base de conhecimento atingido.");
    }
}
