package com.guminteligencia.ura_chatbot_ia.domain.dashboard;

import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.dashboard.ChartDataResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChartDataResponse {

    private List<ChartDataResponse.ChartItem> items;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChartItem {
        private String label;
        private Number value;
    }
}
