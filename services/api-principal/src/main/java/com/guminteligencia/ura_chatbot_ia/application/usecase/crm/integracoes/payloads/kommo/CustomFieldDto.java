package com.guminteligencia.ura_chatbot_ia.application.usecase.crm.integracoes.payloads.kommo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class CustomFieldDto {

    @JsonProperty("field_id")
    private Integer fieldId;

    @JsonProperty("values")
    private List<CustomFieldValueDto> values;
}
