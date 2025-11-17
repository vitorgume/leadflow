package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TipoConsulta {
    ESTETICA_FACIAL_CORPORAL(0, "Estética facial ou corporal", 2191686),
    SAUDE_CAPILAR(1, "Saúde capilar", 2191688),
    NAO_INFORMADO(2, "Não informado", 2191690);

    private final int codigo;
    private final String descricao;
    private final int codigoCrm;
}
