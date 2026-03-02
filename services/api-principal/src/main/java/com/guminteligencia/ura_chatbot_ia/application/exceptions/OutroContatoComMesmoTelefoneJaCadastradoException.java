package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class OutroContatoComMesmoTelefoneJaCadastradoException extends RuntimeException {
    public OutroContatoComMesmoTelefoneJaCadastradoException() {
        super("Outro contato com o mesmo telefone já cadastrado.");
    }
}
