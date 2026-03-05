package com.guminteligencia.ura_chatbot_ia.domain.outrosSetores;

import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Setor {
    private UUID id;
    private String nome;
    private String descricao;
    private List<Membro> membros;
    private LocalDateTime dataCriacao;
    private Usuario usuario;

    public void setDados(Setor novosDados) {
        this.nome = novosDados.getNome();
        this.descricao = novosDados.getDescricao();
        this.membros = novosDados.getMembros();
    }
}
