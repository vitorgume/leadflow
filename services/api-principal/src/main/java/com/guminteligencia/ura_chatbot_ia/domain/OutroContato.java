package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class OutroContato {
    private Long id;
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
