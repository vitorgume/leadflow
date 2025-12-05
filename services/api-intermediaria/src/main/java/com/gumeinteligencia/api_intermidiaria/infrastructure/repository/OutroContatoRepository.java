package com.gumeinteligencia.api_intermidiaria.infrastructure.repository;

import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.OutroContatoEntityLeadflow;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
