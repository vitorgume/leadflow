package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;

public interface MensagemGateway {
    void enviar(String resposta, String telefone, String idInstance, String token);

    void enviarContato(String telefone, Cliente cliente, String idInstance, String token);
    void enviarRelatorio(String arquivo, String fileName, String telefone, String idInstance, String token);
}
