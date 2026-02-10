package com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.kommo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class PayloadKommo {
    @JsonProperty("responsible_user_id")
    private Integer responsibleUserId;

    @JsonProperty("status_id")
    private Integer statusId;

    @JsonProperty("custom_fields_values")
    private List<CustomFieldDto> customFieldsValues;

    @JsonProperty("_embedded")
    private Map<String, Object> embedded;
}
