package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.dashboard.ChartDataResponse;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.dashboard.ChartDataResponseDto;

public class ChartDataResponseMapper {

    public static ChartDataResponseDto paraDto(ChartDataResponse domain) {
        return ChartDataResponseDto.builder()
                .items(domain.getItems().stream().map(ChartDataResponseMapper::paraDtoCharItem).toList())
                .build();
    }

    public static ChartDataResponseDto.ChartItemDto paraDtoCharItem(ChartDataResponse.ChartItem domain) {
        return ChartDataResponseDto.ChartItemDto.builder()
                .label(domain.getLabel())
                .value(domain.getValue())
                .build();
    }
}
