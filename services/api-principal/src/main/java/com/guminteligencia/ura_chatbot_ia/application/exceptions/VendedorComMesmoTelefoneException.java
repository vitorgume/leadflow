package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class VendedorComMesmoTelefoneException extends RuntimeException {
    public VendedorComMesmoTelefoneException() {
        super("Vendedor com o mesmo número de telefone já cadastrado.");
    }
}
