package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class MidiaClienteNaoEncontradaException extends RuntimeException {

    public MidiaClienteNaoEncontradaException() {
        super("Mídia do cliente não encontrada.");
    }
}
