package com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.moskit;

import lombok.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class ContatoMoskitDto {
    private Integer id;
    private Map<String, Integer> createdBy;
    private Map<String, Integer> responsible;
    private String name;
    private List<PhoneDto> phones;
}
