package com.guminteligencia.ura_chatbot_ia.entrypoint.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guminteligencia.ura_chatbot_ia.domain.StatusConversa;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactDashboardDto {
    private String nome;
    private String telefone;
    private StatusConversa status;
    @JsonProperty(namespace = "data_horario")
    private LocalDateTime dataHorario;
}
