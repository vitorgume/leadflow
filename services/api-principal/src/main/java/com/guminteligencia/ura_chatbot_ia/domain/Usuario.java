package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Usuario {
    private UUID id;
    private String nome;
    private String telefone;
    private String senha;
    private String email;
}
