package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class UsuarioExistenteException extends RuntimeException {
    public UsuarioExistenteException() {
        super("Usuário já cadastrado com esse mesmo telefone.");
    }
}
