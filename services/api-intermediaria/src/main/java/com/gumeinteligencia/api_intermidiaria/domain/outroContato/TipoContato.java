package com.gumeinteligencia.api_intermidiaria.domain.outroContato;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TipoContato {
    PADRAO(0, "Financeiro"),
    GERENTE(2, "Gerente"),
    CONSULTOR(3, "Outro");

    private final int codigo;
    private final String descricao;
}
