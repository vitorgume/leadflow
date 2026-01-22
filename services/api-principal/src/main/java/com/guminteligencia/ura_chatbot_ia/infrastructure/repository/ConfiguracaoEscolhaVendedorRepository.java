package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.ConfiguracaoEscolhaVendedorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ConfiguracaoEscolhaVendedorRepository extends JpaRepository<ConfiguracaoEscolhaVendedorEntity, UUID> {
    Page<ConfiguracaoEscolhaVendedorEntity> findByUsuario_Id(UUID id, Pageable pageable);

    @Query("SELECT c FROM ConfiguracaoEscolhaVendedor c WHERE c.usuario.id = :id")
    List<ConfiguracaoEscolhaVendedorEntity> findByUsuario(UUID id);
}
