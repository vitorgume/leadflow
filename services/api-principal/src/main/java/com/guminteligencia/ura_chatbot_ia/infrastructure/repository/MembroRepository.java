package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.MembroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembroRepository extends JpaRepository<MembroEntity, UUID> {
    Optional<MembroEntity> findByTelefone(String telefone);

    List<MembroEntity> findByUsuario_Id(UUID idUsuario);
}
