package com.guminteligencia.ura_chatbot_ia.entrypoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guminteligencia.ura_chatbot_ia.domain.Prioridade;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class VendedorDto {
    private Long id;
    private String nome;
    private String telefone;
    private Boolean inativo;
    private Prioridade prioridade;

    @JsonProperty("id_vendedor_crm")
    private Integer idVendedorCrm;

    private Boolean padrao;
}
