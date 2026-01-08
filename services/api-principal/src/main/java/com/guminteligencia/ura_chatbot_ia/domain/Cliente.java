package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.*;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Cliente {
    private UUID id;
    private String nome;
    private String telefone;
    private Map<String, Object> atributosQualificacao;
    private boolean inativo;
    private Usuario usuario;

    public void setDados(Cliente cliente) {
        this.nome = cliente.getNome();
        this.atributosQualificacao = cliente.getAtributosQualificacao();
    }
}
