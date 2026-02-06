package com.gumeinteligencia.api_intermidiaria.infrastructure.repository;

import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.OutroContatoEntityLeadflow;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;
import java.util.Optional;

@Repository
public class OutroContatoRepository {

    private final DynamoDbTable<OutroContatoEntityLeadflow> outroContatoTable;

    public OutroContatoRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.outroContatoTable = dynamoDbEnhancedClient.table("outro_contato_entity_leadflow", TableSchema.fromBean(OutroContatoEntityLeadflow.class));
    }

    public OutroContatoEntityLeadflow salvar(OutroContatoEntityLeadflow outroContato) {
        outroContatoTable.putItem(outroContato);
        return outroContato;
    }

    public List<OutroContatoEntityLeadflow> listar() {
        return outroContatoTable.scan().items().stream().toList();
    }

    public Optional<OutroContatoEntityLeadflow> consultarPorTelefone(String telefone) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(telefone).build());

        return outroContatoTable.index("TelefoneIndex").query(QueryEnhancedRequest.builder().queryConditional(queryConditional).build())
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }
}

