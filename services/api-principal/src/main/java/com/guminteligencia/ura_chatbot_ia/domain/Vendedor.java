package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Vendedor {
    private Long id;
    private String nome;
    private String telefone;
    private Boolean inativo;
    private Prioridade prioridade;
    private Integer idVendedorCrm;
    private Boolean padrao;

    public void setDados(Vendedor novosDados) {
        this.nome = novosDados.getNome();
        this.telefone = novosDados.getTelefone();
        this.inativo = novosDados.getInativo();
        this.prioridade = novosDados.getPrioridade();
        this.idVendedorCrm = novosDados.getIdVendedorCrm();
        this.padrao = novosDados.getPadrao();
    }
}
