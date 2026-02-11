package com.guminteligencia.ura_chatbot_ia.domain.vendedor;

import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
public class Vendedor {
    private Long id;
    private String nome;
    private String telefone;
    private Boolean inativo;
    private Integer idVendedorCrm;
    private Boolean padrao;
    private Usuario usuario;

    public void setDados(Vendedor novosDados) {
        this.nome = novosDados.getNome();
        this.telefone = novosDados.getTelefone();
        this.inativo = novosDados.getInativo();
        this.idVendedorCrm = novosDados.getIdVendedorCrm();
        this.padrao = novosDados.getPadrao();
    }
}
