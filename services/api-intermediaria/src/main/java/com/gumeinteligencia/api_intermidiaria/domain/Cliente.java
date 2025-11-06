package com.gumeinteligencia.api_intermidiaria.domain;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
public class Cliente {
    private UUID id;
    private String nome;
    private String telefone;
    private String cpf;
    private Boolean consentimentoAtendimnento;
    private TipoConsulta tipoConsulta;
    private String dorDesejoPaciente;
    private String linkMidia;
    private PreferenciaHorario preferenciaHorario;
    private boolean inativo;
}
