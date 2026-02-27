package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;


import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.OutroContatoEntity;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public class OutroContatoRepositoryNoSql {

    private final DynamoDbTable<OutroContatoEntity> outroContatoTable;

    public OutroContatoRepositoryNoSql(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.outroContatoTable = dynamoDbEnhancedClient.table("outro_contato_entity", TableSchema.fromBean(OutroContatoEntity.class));
    }

    public OutroContatoEntity salvar(OutroContatoEntity outroContato) {
        outroContatoTable.putItem(outroContato);
        return outroContato;
    }

    public Optional<OutroContatoEntity> consultarPorTelefoneEUsuario(String telefone, String idUsuario) {
        Key key = Key.builder()
                .partitionValue(idUsuario)
                .sortValue(telefone)
                .build();

        QueryConditional queryConditional = QueryConditional.keyEqualTo(key);

        return outroContatoTable.index("UsuarioTelefoneIndex")
                .query(QueryEnhancedRequest.builder()
                        .queryConditional(queryConditional)
                        .build())
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }

    public void deletar(UUID id) {
        Key key = Key.builder()
                .partitionValue(id.toString())
                .build();

        outroContatoTable.deleteItem(key);
    }
}
