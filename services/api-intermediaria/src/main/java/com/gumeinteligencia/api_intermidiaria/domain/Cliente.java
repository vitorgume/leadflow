package com.gumeinteligencia.api_intermidiaria.domain;

import lombok.*;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
public class Cliente {
    private UUID id;
    private String nome;
    private String telefone;
    private Map<String, Object> atributosQualificacao;
    private boolean inativo;
}
