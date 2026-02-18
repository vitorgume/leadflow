package com.guminteligencia.ura_chatbot_ia.entrypoint.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MetricResponseDto<T> {
    private T value;
}
