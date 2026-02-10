package com.guminteligencia.ura_chatbot_ia.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
public class Qualificacao {

    private String nome;
    private Map<String, Object> atributosVariaveis;
}
