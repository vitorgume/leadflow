package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PreferenciaHorario {
    MANHA(0, "8h as 12h", 2191868),
    TARDE(1, "13h as 18h", 2191870),
    QUALQUER_HORARIO(2, "Qualquer horario", 2191872),
    NAO_INFORMADO(3, "Não informado", 2191874);

    private final int codigo;
    private final String descricao;
    private final int codigoCrm;
}
