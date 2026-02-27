package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;

import com.guminteligencia.ura_chatbot_ia.domain.TipoContato;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.OutroContatoEntitySql;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OutroContatoRepository extends JpaRepository<OutroContatoEntitySql, Long> {
    Optional<OutroContatoEntitySql> findByNome(String nome);

    List<OutroContatoEntitySql> findByTipoContatoAndUsuario_Id(TipoContato tipo, UUID id);

    Optional<OutroContatoEntitySql> findByTelefoneAndUsuario_Id(String telefone, UUID idUsuario);

    Page<OutroContatoEntitySql> findByUsuario_Id(Pageable pageable, UUID idUsuario);
}
