package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.SetorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SetorRepository extends JpaRepository<SetorEntity, UUID> {
    Optional<SetorEntity> findByNomeAndUsuario_Id(String nome, UUID idUsuario);

    List<SetorEntity> findByUsuario_Id(UUID idUsuario);
}
