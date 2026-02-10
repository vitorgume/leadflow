package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class OutroContatoTipoGerenciaJaCadastradoException extends RuntimeException {
    public OutroContatoTipoGerenciaJaCadastradoException() {
        super("Outro contato do tipo gerencia já cadastrado.");
    }
}
