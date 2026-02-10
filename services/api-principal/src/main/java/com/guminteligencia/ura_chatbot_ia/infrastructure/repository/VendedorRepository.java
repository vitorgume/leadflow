package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.vendedor.VendedorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VendedorRepository extends JpaRepository<VendedorEntity, Long> {
    Optional<VendedorEntity> findByNome(String nome);

    @Query("SELECT v FROM Vendedor v WHERE v.nome <> :excecao")
    List<VendedorEntity> listarComExcecao(String excecao);

    Optional<VendedorEntity> findByTelefone(String telefone);

    List<VendedorEntity> findByInativoIsFalse();

    Optional<VendedorEntity> findByPadraoIsTrue();

    List<VendedorEntity> findByUsuario_Id(UUID id);
}
