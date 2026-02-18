package com.guminteligencia.ura_chatbot_ia.entrypoint.dto.dashboard;

import com.guminteligencia.ura_chatbot_ia.domain.StatusConversa;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactDashboardDto {
    private String nome;
    private String telefone;
    private StatusConversa status;
}
