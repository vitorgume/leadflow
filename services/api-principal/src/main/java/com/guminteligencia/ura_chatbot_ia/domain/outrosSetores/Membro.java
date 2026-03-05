package com.guminteligencia.ura_chatbot_ia.domain.outrosSetores;

import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
public class Membro {
    private UUID id;
    private String nome;
    private String telefone;
    private Usuario usuario;

    public void setDados(Membro novosDados) {
        this.nome = novosDados.getNome();
        this.telefone = novosDados.getTelefone();
    }
}
