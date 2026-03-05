package com.gumeinteligencia.api_intermidiaria.domain;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
@ToString
public class Usuario {
    private UUID id;
    private String nome;
    private String telefone;
    private String email;
    private String telefoneConectado;
    private Boolean softwareLigado;
}
