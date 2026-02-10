package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class NenhumVendedorReferenciadoException extends RuntimeException {
    public NenhumVendedorReferenciadoException() {
        super("Nenhum vendedor referênciado para a configuração de escolha dos vendedores.");
    }
}
