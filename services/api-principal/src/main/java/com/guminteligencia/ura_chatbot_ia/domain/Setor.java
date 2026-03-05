package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class Setor {
    private UUID id;
    private String nome;
    private String descricao;
    private List<Membro> membros;
    private LocalDateTime dataCriacao;
    private Usuario usuario;
}
