package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TipoConsulta {
    ESTETICA_FACIAL_CORPORAL(0, "Estética facial ou corporal"),
    SAUDE_CAPILAR(1, "Saúde capilar"),
    NAO_INFORMADO(2, "Não informado");

    private final int codigo;
    private final String descricao;
}
