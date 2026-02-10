package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class ConfiguraCrmUsuarioNaoConfiguradaException extends RuntimeException {
    public ConfiguraCrmUsuarioNaoConfiguradaException() {
        super("Configuração de crm do usuário não configurado.");
    }
}
