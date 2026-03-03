package com.guminteligencia.ura_chatbot_ia.domain.dashboard;

import com.guminteligencia.ura_chatbot_ia.domain.StatusConversa;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactDashboard {
    private String nome;
    private String telefone;
    private StatusConversa status;
    private LocalDateTime dataHorario;
}
