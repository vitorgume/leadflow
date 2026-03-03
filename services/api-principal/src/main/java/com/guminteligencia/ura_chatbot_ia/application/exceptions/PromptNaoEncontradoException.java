package com.guminteligencia.ura_chatbot_ia.application.exceptions;

import com.guminteligencia.ura_chatbot_ia.domain.Prompt;

public class PromptNaoEncontradoException extends RuntimeException {
    public PromptNaoEncontradoException() {
        super("Prompt não encontrado.");
    }
}
