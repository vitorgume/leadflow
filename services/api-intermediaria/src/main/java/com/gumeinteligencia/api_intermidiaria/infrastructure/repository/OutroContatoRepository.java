package com.gumeinteligencia.api_intermidiaria.infrastructure.repository;

import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.OutroContatoEntityLeadflow;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OutroContatoRepository {

    private final DynamoDbTemplate dynamoDbTemplate;


    public OutroContatoEntityLeadflow salvar(OutroContatoEntityLeadflow outroContato) {
        return dynamoDbTemplate.save(outroContato);
    }

    public List<OutroContatoEntityLeadflow> listar() {
        return dynamoDbTemplate.scanAll(OutroContatoEntityLeadflow.class)
                .stream()
                .flatMap(page -> page.items().stream())
                .toList();
    }

    public Optional<OutroContatoEntityLeadflow> consultarPorTelefone(String telefone) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(telefone).build());
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();

        return dynamoDbTemplate.query(queryRequest, OutroContatoEntityLeadflow.class)
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }
}

