package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class UsuarioNaoEncotradoException extends RuntimeException {
    public UsuarioNaoEncotradoException() {
        super("Usuário não encontrado");
    }
}
