package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.StatusConversa;
import com.guminteligencia.ura_chatbot_ia.domain.dashboard.ContactDashboard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DashboardDataGateway {
    List<ConversaAgente> getContactsByDay(Integer year, Integer month, String ddd, StatusConversa status, UUID idUsuario);

    List<ConversaAgente> getContactsByHour(Integer year, Integer month, Integer day, String ddd, StatusConversa status, UUID idUsuario);

    Page<ContactDashboard> getPaginatedContacts(Integer year, Integer month, Integer day, String ddd, StatusConversa status, Pageable pageable, UUID idUsuario);

    List<ConversaAgente> getAverageContactsPerSeller(Integer year, Integer month, Integer day, String ddd, StatusConversa status, UUID idUsuario);

    List<ConversaAgente> getResponseRate(Integer year, Integer month, Integer day, String ddd, StatusConversa status, UUID idUsuario);

    Long count(Integer year, Integer month, Integer day, String ddd, StatusConversa status, UUID idUsuario);

    Long count(LocalDate today, UUID idUsuario);
}
