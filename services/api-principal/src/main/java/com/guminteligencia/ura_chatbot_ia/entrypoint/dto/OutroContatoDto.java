package com.guminteligencia.ura_chatbot_ia.entrypoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guminteligencia.ura_chatbot_ia.domain.TipoContato;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class OutroContatoDto {
    private UUID id;
    private String nome;
    private String telefone;
    private String descricao;
    @JsonProperty("tipo_contato")
    private TipoContato tipoContato;
    private UsuarioDto usuario;
}
