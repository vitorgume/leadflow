package com.gumeinteligencia.api_intermidiaria.application.exceptions;

public class ConversaAgenteNaoEncontradoException extends RuntimeException {

    public ConversaAgenteNaoEncontradoException() {
        super("Conversa não encontrada.");
    }
}
