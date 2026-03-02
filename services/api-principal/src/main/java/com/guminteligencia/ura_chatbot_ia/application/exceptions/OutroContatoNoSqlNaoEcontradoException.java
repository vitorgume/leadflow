package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class OutroContatoNoSqlNaoEcontradoException extends RuntimeException {
    public OutroContatoNoSqlNaoEcontradoException() {
        super("Outro contato NoSQL não encontrado.");
    }
}
