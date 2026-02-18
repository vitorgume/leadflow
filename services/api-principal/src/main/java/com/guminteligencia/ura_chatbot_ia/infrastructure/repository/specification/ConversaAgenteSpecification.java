package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.specification;

import com.guminteligencia.ura_chatbot_ia.domain.StatusConversa;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConversaAgenteEntity;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConversaAgenteSpecification {

    public static Specification<ConversaAgenteEntity> filterBy(
            Integer year, Integer month, Integer day, String ddd, StatusConversa status, UUID idUsuario) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (year != null) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class, root.get("dataCriacao")), year));
            }
            if (month != null) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.function("MONTH", Integer.class, root.get("dataCriacao")), month));
            }
            if (day != null) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.function("DAY", Integer.class, root.get("dataCriacao")), day));
            }
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (ddd != null && !ddd.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("cliente").get("telefone"), ddd + "%"));
            }
            if (idUsuario != null) {
                predicates.add(criteriaBuilder.equal(root.get("cliente").get("usuario").get("id"), idUsuario));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
