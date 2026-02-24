package com.guminteligencia.ura_chatbot_ia.domain;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.BaseConhecimentoEntity;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
public class BaseConhecimento {
    private UUID id;
    private Usuario usuario;
    private String titulo;
    private String conteudo;

    public void setDados(BaseConhecimento baseConhecimento) {
        this.titulo = baseConhecimento.getTitulo();
        this.conteudo = baseConhecimento.getConteudo();
    }
}
