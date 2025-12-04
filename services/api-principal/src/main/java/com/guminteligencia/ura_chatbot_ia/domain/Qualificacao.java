package com.guminteligencia.ura_chatbot_ia.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
public class Qualificacao {

    private String nome;
    private String cpf;

    @JsonProperty("consentimento_atendimento")
    private Boolean consentimentoAtendimnento;

    @JsonProperty("tipo_consulta")
    private Integer tipoConsulta;

    @JsonProperty("dor_desejo_paciente")
    private String dorDesejoPaciente;

    @JsonProperty("preferencia_horario")
    private Integer preferenciaHorario;
}
