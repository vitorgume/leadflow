package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ObjetoRelatorioEntity {
    private String nome;
    private String telefone;
    private Map<String, String> atributos_qualificacao;
    private LocalDateTime data_criacao;
    private String nome_vendedor;
}
