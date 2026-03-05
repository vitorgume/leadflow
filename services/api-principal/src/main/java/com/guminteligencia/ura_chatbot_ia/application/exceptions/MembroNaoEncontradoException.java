package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class MembroNaoEncontradoException extends RuntimeException {
    public MembroNaoEncontradoException() {
        super("Membro não encontrado.");
    }
}
