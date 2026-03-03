package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class ConfiguracaoCrmUsuarioNaoConfiguradaException extends RuntimeException {
    public ConfiguracaoCrmUsuarioNaoConfiguradaException() {
        super("Configuração de CRM do usuário não realizada.");
    }
}
