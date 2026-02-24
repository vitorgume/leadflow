package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.PromptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PromptRepository extends JpaRepository<PromptEntity, UUID> {
    List<PromptEntity> findByUsuario_Id(UUID idUsuario);
}
