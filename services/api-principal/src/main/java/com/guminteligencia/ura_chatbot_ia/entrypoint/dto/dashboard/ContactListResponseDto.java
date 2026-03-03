package com.guminteligencia.ura_chatbot_ia.entrypoint.dto.dashboard;

import com.guminteligencia.ura_chatbot_ia.domain.StatusConversa;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactListResponseDto {

    private List<ContactDashboardDto> contacts;
    private int totalPages;
    private long totalElements;

    public static ContactListResponseDto fromPage(Page<ContactDashboardDto> page) {
        return new ContactListResponseDto(page.getContent(), page.getTotalPages(), page.getTotalElements());
    }
}
