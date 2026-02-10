package com.guminteligencia.ura_chatbot_ia.entrypoint.dto.vendedor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.ConectorLogico;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.OperadorLogico;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class CondicaoDto {
    private UUID id;
    private String campo;

    @JsonProperty("operador_logico")
    private OperadorLogico operadorLogico;
    private String valor;

    @JsonProperty("conector_logico")
    private ConectorLogico conectorLogico;
}
