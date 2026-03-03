package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
public class OutroContato {
    private UUID id;
    private String nome;
    private String telefone;
    private String descricao;
    private TipoContato tipoContato;
    private Usuario usuario;

    public void setDados(OutroContato novosDados) {
        this.nome = novosDados.getNome();
        this.telefone = novosDados.getTelefone();
        this.descricao = novosDados.getDescricao();
        this.tipoContato = novosDados.getTipoContato();
    }
}
