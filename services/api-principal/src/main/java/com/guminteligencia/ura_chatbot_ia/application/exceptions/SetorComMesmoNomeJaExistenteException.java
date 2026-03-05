package com.guminteligencia.ura_chatbot_ia.application.exceptions;

public class SetorComMesmoNomeJaExistenteException extends RuntimeException {
    public SetorComMesmoNomeJaExistenteException() {
        super("Setor com o mesmo nome ja existente.");
    }
}
