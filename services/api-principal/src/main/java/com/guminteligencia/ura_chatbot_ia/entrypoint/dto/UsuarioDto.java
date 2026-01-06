package com.guminteligencia.ura_chatbot_ia.entrypoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class UsuarioDto {
    private UUID id;
    private String nome;
    private String telefone;
    private String senha;
    private String email;
}
