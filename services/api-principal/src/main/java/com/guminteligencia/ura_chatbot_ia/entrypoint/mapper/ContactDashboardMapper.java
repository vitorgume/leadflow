package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.dashboard.ContactDashboard;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.dashboard.ContactDashboardDto;

public class ContactDashboardMapper {

    public static ContactDashboardDto paraDto(ContactDashboard domain) {
        return ContactDashboardDto.builder()
                .nome(domain.getNome())
                .telefone(domain.getTelefone())
                .status(domain.getStatus())
                .dataHorario(domain.getDataHorario())
                .build();
    }
}
