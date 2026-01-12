package com.guminteligencia.ura_chatbot_ia.application.gateways;

public interface CriptografiaJCAGateway {
    String criptografar(String chave);

    String descriptografar(String chave);
}
