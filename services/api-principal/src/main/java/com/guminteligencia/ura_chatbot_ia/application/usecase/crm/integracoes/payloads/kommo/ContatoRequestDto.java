package com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.kommo;

import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ContatoRequestDto {
    private String phone;
    private String contactName;
    private String contactPhone;
}
