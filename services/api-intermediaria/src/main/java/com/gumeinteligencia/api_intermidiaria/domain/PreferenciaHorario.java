package com.gumeinteligencia.api_intermidiaria.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PreferenciaHorario {
    MANHA(0, "8h as 12h"),
    TARDE(1, "13h as 18h"),
    QUALQUER_HORARIO(2, "Qualquer horario");

    private int codigo;
    private String descricao;
}
