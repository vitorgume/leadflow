package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.gateways.CriptografiaJCAGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CriptografiaJCAUseCase {

    private final CriptografiaJCAGateway gateway;

    public String criptografar(String chave) {
        return gateway.criptografar(chave);
    }

    public String descriptografar(String chave) {
        return gateway.descriptografar(chave);
    }

}
