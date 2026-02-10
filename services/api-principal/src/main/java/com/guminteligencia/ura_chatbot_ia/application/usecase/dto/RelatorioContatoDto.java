package com.guminteligencia.ura_chatbot_ia.application.usecase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class RelatorioContatoDto {
    private String nome;
    private String telefone;
    private String cpf;
    private Boolean consentimentoAtendimnento;
    private String dorDesejoPaciente;
    private String linkMidia;
    private LocalDateTime dataCriacao;
    private String nomeVendedor;
}
