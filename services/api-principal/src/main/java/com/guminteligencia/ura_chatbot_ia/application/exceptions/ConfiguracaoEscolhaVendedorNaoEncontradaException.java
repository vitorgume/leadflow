package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class ConfiguracaoEscolhaVendedorNaoEncontradaException extends RuntimeException {

    public ConfiguracaoEscolhaVendedorNaoEncontradaException() {
        super("Configuração de escolha de vendedor não encontrada.");
    }
}
