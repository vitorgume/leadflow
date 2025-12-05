package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ContextoEntityLeadflow;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContextoRepository {

    private final DynamoDbTemplate dynamoDbTemplate;

    public void deletar(ContextoEntityLeadflow contextoEntityLeadflow) {
        dynamoDbTemplate.delete(contextoEntityLeadflow);
    }

    public Optional<ContextoEntityLeadflow> consultarPorId(UUID id) {
        ContextoEntityLeadflow contexto = dynamoDbTemplate.load(Key.builder()
                        .partitionValue(id.toString())
                        .build()
                , ContextoEntityLeadflow.class
        );

        return contexto == null ? Optional.empty() : Optional.of(contexto);
    }
}
