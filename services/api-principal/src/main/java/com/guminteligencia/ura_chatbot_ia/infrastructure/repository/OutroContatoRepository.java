package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;

import com.guminteligencia.ura_chatbot_ia.domain.TipoContato;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.OutroContatoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OutroContatoRepository extends JpaRepository<OutroContatoEntity, Long> {
    Optional<OutroContatoEntity> findByNome(String nome);

    Optional<OutroContatoEntity> findByTipoContatoAndUsuario_Id(TipoContato tipo, UUID id);

    Optional<OutroContatoEntity> findByTelefone(String telefone);

    Page<OutroContatoEntity> findByUsuario_Id(Pageable pageable, UUID idUsuario);
}
