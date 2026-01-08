package com.guminteligencia.ura_chatbot_ia.entrypoint.dto;

import com.guminteligencia.ura_chatbot_ia.domain.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ClienteDto {
    private UUID id;
    private String nome;
    private String telefone;
    private Map<String, Object> atributosQualificacao;
    private UsuarioDto usuario;
    private boolean inativo;
}
