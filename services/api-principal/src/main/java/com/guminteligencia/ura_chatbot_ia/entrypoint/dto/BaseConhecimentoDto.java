package com.guminteligencia.ura_chatbot_ia.entrypoint.dto;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class BaseConhecimentoDto {
    private UUID id;
    private UsuarioDto usuario;
    private String titulo;
    private String conteudo;
}
