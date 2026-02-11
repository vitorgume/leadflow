package com.gumeinteligencia.api_intermidiaria.infrastructure.repository;

import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.ConversaAgenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversaAgenteRepository extends JpaRepository<ConversaAgenteEntity, UUID> {
    Optional<ConversaAgenteEntity> findByCliente_Telefone(String telefone);
}
