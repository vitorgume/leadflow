
package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.gateways.DashboardDataGateway;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.StatusConversa;
import com.guminteligencia.ura_chatbot_ia.domain.dashboard.ChartDataResponse;
import com.guminteligencia.ura_chatbot_ia.domain.dashboard.ContactDashboard;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardUseCase {

    private final DashboardDataGateway gateway;

    public Long getTotalContacts(Integer year, Integer month, Integer day, String ddd, StatusConversa status, UUID idUsuario) {
        return gateway.count(year, month, day, ddd, status, idUsuario);
    }

    public Long getContactsToday(UUID idUsuario) {
        LocalDate today = LocalDate.now();
        return gateway.count(today, idUsuario);
    }

    public Double getResponseRate(Integer year, Integer month, Integer day, String ddd, StatusConversa status, UUID idUsuario) {
        List<ConversaAgente> conversations = gateway.getResponseRate(year, month, day, ddd, status, idUsuario);

        long activeOrInProgress = conversations.stream()
                .filter(c -> c.getStatus() == StatusConversa.ATIVO || c.getStatus() == StatusConversa.ANDAMENTO)
                .count();

        long inactive = conversations.stream()
                .filter(c -> c.getStatus() == StatusConversa.INATIVO_G1 || c.getStatus() == StatusConversa.INATIVO_G2)
                .count();

        long total = activeOrInProgress + inactive;
        return total == 0 ? 0 : (double) activeOrInProgress / total;
    }

    public double getAverageContactsPerSeller(Integer year, Integer month, Integer day, String ddd, StatusConversa status, UUID idUsuario) {
        List<ConversaAgente> conversations = gateway.getAverageContactsPerSeller(year, month, day, ddd, status, idUsuario);

        if (conversations.isEmpty()) {
            return 0;
        }
        Long totalSellers = conversations.stream()
                                         .map(ConversaAgente::getVendedor)
                                         .filter(v -> v != null)
                                         .distinct()
                                         .count();
        return totalSellers == 0 ? 0 : (double) conversations.size() / totalSellers;
    }

    public ChartDataResponse getContactsByDay(Integer year, Integer month, String ddd, StatusConversa status, UUID idUsuario) {
        List<ConversaAgente> conversations = gateway.getContactsByDay(year, month, ddd, status, idUsuario);

        Map<Integer, Long> contactsByDay = conversations.stream()
                .collect(Collectors.groupingBy(c -> c.getDataCriacao().getDayOfMonth(), Collectors.counting()));

        List<ChartDataResponse.ChartItem> chartItems = contactsByDay.entrySet().stream()
                .map(entry -> new ChartDataResponse.ChartItem(entry.getKey().toString(), entry.getValue()))
                .collect(Collectors.toList());

        return new ChartDataResponse(chartItems);
    }

    public ChartDataResponse getContactsByHour(Integer year, Integer month, Integer day, String ddd, StatusConversa status, UUID idUsuario) {
        List<ConversaAgente> conversations = gateway.getContactsByHour(year, month, day, ddd, status, idUsuario);

        Map<Integer, Long> contactsByHour = conversations.stream()
                .collect(Collectors.groupingBy(c -> c.getDataCriacao().getHour(), Collectors.counting()));

        List<ChartDataResponse.ChartItem> chartItems = contactsByHour.entrySet().stream()
                .map(entry -> new ChartDataResponse.ChartItem(entry.getKey().toString(), entry.getValue()))
                .collect(Collectors.toList());

        return new ChartDataResponse(chartItems);
    }

    public Page<ContactDashboard> getPaginatedContacts(Integer year, Integer month, Integer day, String ddd, StatusConversa status, Pageable pageable, UUID idUsuario) {
        return gateway.getPaginatedContacts(year, month, day, ddd, status, pageable, idUsuario);
    }
}
