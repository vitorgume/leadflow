package com.guminteligencia.ura_chatbot_ia.entrypoint.dto.dashboard;

import com.guminteligencia.ura_chatbot_ia.domain.StatusConversa;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardRequestDto {
    private Integer day;
    private Integer month;
    private Integer year;
    private String ddd;
    private StatusConversa status;
    private UUID idUsuario;
}
