package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConversaAgenteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversaAgenteGateway {
    ConversaAgente salvar(ConversaAgente conversaAgente);

    Optional<ConversaAgente> consultarPorIdCliente(UUID id);

    Optional<ConversaAgente> consultarPorId(UUID idConversa);

    List<ConversaAgente> listarNaoFinalizados();

    Long count(Specification<ConversaAgenteEntity> spec);

    Page<ConversaAgenteEntity> findAllPage(Specification<ConversaAgenteEntity> baseSpec, Pageable pageable);

    List<ConversaAgenteEntity> findAllList(Specification<ConversaAgenteEntity> spec);
}
