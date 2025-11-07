package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PreferenciaHorario {
    MANHA(0, "8h as 12h"),
    TARDE(1, "13h as 18h"),
    QUALQUER_HORARIO(2, "Qualquer horario"),
    NAO_INFORMADO(3, "Não informado");

    private final int codigo;
    private final String descricao;
}
