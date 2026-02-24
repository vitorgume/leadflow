package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class BaseConhecimentoNaoEncontradoException extends RuntimeException {
    public BaseConhecimentoNaoEncontradoException() {
        super("Base de conhecimento não encontrada.");
    }
}
