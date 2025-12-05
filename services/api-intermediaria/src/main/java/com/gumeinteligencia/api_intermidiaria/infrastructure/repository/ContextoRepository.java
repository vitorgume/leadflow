package com.gumeinteligencia.api_intermidiaria.infrastructure.repository;

import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.ContextoEntityLeadflow;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContextoRepository {

    private final DynamoDbTemplate dynamoDbTemplate;

    public ContextoEntityLeadflow salvar(ContextoEntityLeadflow contexto) {
            return dynamoDbTemplate.save(contexto);
    }

    public Optional<ContextoEntityLeadflow> buscarPorId(String id) {
        ContextoEntityLeadflow contexto = dynamoDbTemplate.load(Key.builder()
                .partitionValue(id)
                .build()
                , ContextoEntityLeadflow.class
        );

        return contexto == null ? Optional.empty() : Optional.of(contexto);
    }

    public Optional<ContextoEntityLeadflow> buscarPorTelefone(String telefone) {
        PageIterable<ContextoEntityLeadflow> results = dynamoDbTemplate.scan(ScanEnhancedRequest.builder().build(), ContextoEntityLeadflow.class);

        return results.items()
                .stream()
                .filter(c -> telefone.equals(c.getTelefone()))
                .findFirst();
    }
}
