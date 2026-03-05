package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class SetorNaoEncontradoException extends RuntimeException {
    public SetorNaoEncontradoException() {
        super("Setor não encontrado.");
    }
}
