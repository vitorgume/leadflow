package com.gumeinteligencia.api_intermidiaria.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TipoConsulta {
    ESTETICA_FACIAL_CORPORAL(0),
    SAUDE_CAPILAR(1);

    private int codigo;
}
