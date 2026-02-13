package com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.moskit;

import lombok.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
public class PayloadMoskit {
    private Map<String, Integer> createdBy;
    private Map<String, Integer> responsible;
    private String name;
    private String status;
    private List<ContatoMoskitDto> contacts;
    private Map<String, Integer> stage;
    private List<EntityCustomField> entityCustomFields;
}
