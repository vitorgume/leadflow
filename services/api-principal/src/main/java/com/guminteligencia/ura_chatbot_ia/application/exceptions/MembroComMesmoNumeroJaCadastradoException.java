package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class MembroComMesmoNumeroJaCadastradoException extends RuntimeException {
    public MembroComMesmoNumeroJaCadastradoException() {
        super("Membro com este número já cadastrado.");
    }
}
