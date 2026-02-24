package com.guminteligencia.ura_chatbot_ia.domain;


import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
public class Prompt {
    private UUID id;
    private Usuario usuario;
    private String titulo;
    private String prompt;

    public void setDados(Prompt prompt) {
        this.titulo = prompt.getTitulo();
        this.prompt = prompt.getPrompt();
    }
}
