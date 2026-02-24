package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.DashboardDataGateway;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.StatusConversa;
import com.guminteligencia.ura_chatbot_ia.domain.dashboard.ContactDashboard;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.ConversaAgenteMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConversaAgenteEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.specification.ConversaAgenteSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DashboardDataProvider implements DashboardDataGateway {

    private final ConversaAgenteDataProvider conversaAgenteDataProvider;

    @Override
    public Page<ContactDashboard> getPaginatedContacts(Integer year, Integer month, Integer day, String ddd, StatusConversa status, Pageable pageable, UUID idUsuario) {
        Specification<ConversaAgenteEntity> spec = ConversaAgenteSpecification.filterBy(year, month, day, ddd, status, idUsuario);
        Page<ConversaAgenteEntity> page = conversaAgenteDataProvider.findAllPage(spec, pageable);
        return page.map(this::convertToContactDto);
    }

    @Override
    public List<ConversaAgente> getAverageContactsPerSeller(Integer year, Integer month, Integer day, String ddd, StatusConversa status, UUID idUsuario) {
        Specification<ConversaAgenteEntity> spec = ConversaAgenteSpecification.filterBy(year, month, day, ddd, status, idUsuario);
        List<ConversaAgenteEntity> conversations = conversaAgenteDataProvider.findAllList(spec);
        return conversations.stream().map(ConversaAgenteMapper::paraDomain).toList();
    }

    @Override
    public List<ConversaAgente> getResponseRate(Integer year, Integer month, Integer day, String ddd, StatusConversa status, UUID idUsuario) {
        Specification<ConversaAgenteEntity> baseSpec = ConversaAgenteSpecification.filterBy(year, month, day, ddd, status, idUsuario);
        List<ConversaAgenteEntity> conversations = conversaAgenteDataProvider.findAllList(baseSpec);
        return conversations.stream().map(ConversaAgenteMapper::paraDomain).toList();
    }

    @Override
    public Long count(Integer year, Integer month, Integer day, String ddd, StatusConversa status, UUID idUsuario) {
        Specification<ConversaAgenteEntity> spec = ConversaAgenteSpecification.filterBy(year, month, day, ddd, status, idUsuario);
        return conversaAgenteDataProvider.count(spec);
    }

    @Override
    public Long count(LocalDate today, UUID idUsuario) {
        Specification<ConversaAgenteEntity> spec = ConversaAgenteSpecification.filterBy(
                today.getYear(), today.getMonthValue(), today.getDayOfMonth(), null, null, idUsuario);
        return conversaAgenteDataProvider.count(spec);
    }


    @Override
    public List<ConversaAgente> getContactsByHour(Integer year, Integer month, Integer day, String ddd, StatusConversa status, UUID idUsuario) {
        Specification<ConversaAgenteEntity> spec = ConversaAgenteSpecification.filterBy(year, month, day, ddd, status, idUsuario);
        List<ConversaAgenteEntity> conversations = conversaAgenteDataProvider.findAllList(spec);
        return conversations.stream().map(ConversaAgenteMapper::paraDomain).toList();
    }

    @Override
    public List<ConversaAgente> getContactsByDay(Integer year, Integer month, String ddd, StatusConversa status, UUID idUsuario) {
        Specification<ConversaAgenteEntity> spec = ConversaAgenteSpecification.filterBy(year, month, null, ddd, status, idUsuario);        List<ConversaAgenteEntity> conversations = conversaAgenteDataProvider.findAllList(spec);
        return conversations.stream().map(ConversaAgenteMapper::paraDomain).toList();
    }

    private ContactDashboard convertToContactDto(ConversaAgenteEntity conversaAgente) {
        return new ContactDashboard(
                conversaAgente.getCliente().getNome(),
                conversaAgente.getCliente().getTelefone(),
                conversaAgente.getStatus()
        );
    }
}
