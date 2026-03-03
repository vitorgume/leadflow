package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.BaseConhecimentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BaseConhecimentoRepository extends JpaRepository<BaseConhecimentoEntity, UUID> {
    List<BaseConhecimentoEntity> findByUsuario_Id(UUID idUsuario);
}
