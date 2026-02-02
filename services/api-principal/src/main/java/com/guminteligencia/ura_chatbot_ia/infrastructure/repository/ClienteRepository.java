package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ClienteEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ObjetoRelatorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, UUID> {
    Optional<ClienteEntity> findByTelefone(String telefone);

    @Query(value = """
                  (
                  	SELECT cl.nome, cl.telefone, cl.atributos_qualificacao, co.data_criacao, v.nome AS nome_vendedor
                  	FROM clientes cl
                  	INNER JOIN conversas_agente co ON co.cliente_id_cliente = cl.id_cliente
                  	INNER JOIN vendedores v ON v.id_vendedor = co.vendedor_id_vendedor
                  	WHERE co.data_criacao >= DATE_FORMAT(DATE_SUB(DATE_ADD(NOW(), INTERVAL 3 HOUR), INTERVAL 1 DAY), '%Y-%m-%d 16:00:00')
                  	  AND co.data_criacao <= DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 3 HOUR), '%Y-%m-%d 23:59:59')
                        AND cl.usuario_id = :idUsuario
                  )
                  UNION ALL
                  (
                  	SELECT cl.nome, cl.telefone, cl.atributos_qualificacao, co.data_criacao, v.nome AS nome_vendedor
                  	FROM clientes cl
                  	INNER JOIN conversas_agente co ON co.cliente_id_cliente = cl.id_cliente
                  	INNER JOIN vendedores v ON v.id_vendedor = co.vendedor_id_vendedor
                  	WHERE co.data_criacao >= DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 3 HOUR), '%Y-%m-%d 00:00:00')
                  	  AND co.data_criacao <= DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 3 HOUR), '%Y-%m-%d 16:00:00')
                        AND cl.usuario_id = :idUsuario
                  );
            """, nativeQuery = true)
    List<ObjetoRelatorioEntity> gerarRelatorio(UUID idUsuario);

    @Query(value = """
                SELECT cl.nome, cl.telefone, cl.atributos_qualificacao, co.data_criacao, v.nome as nome_vendedor
                                FROM clientes cl
                                INNER JOIN conversas_agente co ON co.cliente_id_cliente = cl.id_cliente
                                INNER JOIN vendedores v ON v.id_vendedor = co.vendedor_id_vendedor
                                WHERE co.data_criacao >= DATE_FORMAT(DATE_SUB(DATE_ADD(NOW(), INTERVAL 3 HOUR), INTERVAL 3 DAY), '%Y-%m-%d 16:00:00')
                                  AND co.data_criacao <= DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 3 HOUR), '%Y-%m-%d 16:00:00')
                                  AND cl.usuario_id = :idUsuario
            """, nativeQuery = true)
    List<ObjetoRelatorioEntity> gerarRelatorioSegundaFeira(UUID idUsuario);

    Optional<ClienteEntity> findByTelefoneAndUsuario_Id(String telefone, UUID usuarioId);
}
