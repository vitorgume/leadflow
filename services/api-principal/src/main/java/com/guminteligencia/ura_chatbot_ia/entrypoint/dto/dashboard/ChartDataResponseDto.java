package com.guminteligencia.ura_chatbot_ia.entrypoint.dto.dashboard;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChartDataResponseDto {

    private List<ChartItemDto> items;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ChartItemDto {
        private String label;
        private Number value;
    }
}
