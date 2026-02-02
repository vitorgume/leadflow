package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class OutroContatoComMesmoTelefoneJaCadastradoExcetion extends RuntimeException {
    public OutroContatoComMesmoTelefoneJaCadastradoExcetion() {
        super("Outro contato com o mesmo telefone já cadastrado.");
    }
}
