package com.gumeinteligencia.api_intermidiaria.infrastructure.dataprovider;

import com.gumeinteligencia.api_intermidiaria.application.gateways.ContextoGateway;
import com.gumeinteligencia.api_intermidiaria.domain.Contexto;
import com.gumeinteligencia.api_intermidiaria.domain.MensagemContexto;
import com.gumeinteligencia.api_intermidiaria.domain.StatusContexto;
import com.gumeinteligencia.api_intermidiaria.infrastructure.exceptions.DataProviderException;
import com.gumeinteligencia.api_intermidiaria.infrastructure.mapper.ContextoMapper;
import com.gumeinteligencia.api_intermidiaria.infrastructure.mapper.MensagemContextoListConverter;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.ContextoRepository;
import com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity.ContextoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContextoDataProvider implements ContextoGateway {

    private final ContextoRepository repository;
    private final DynamoDbClient dynamoDbClient;
    private final MensagemContextoListConverter mensagemContextoListConverter = new MensagemContextoListConverter();
    private final String MENSAGEM_ERRO_CONSULTAR_CONTEXTO_PELO_TELEFONE_E_ATIVO = "Erro ao consultar contexto pelo seu telefone e ativo.";
    private final String MENSAGEM_ERRO_SALVAR_CONTEXTO = "Erro ao salvar contexto.";

    @Value("app.dynamo.contexto-table")
    private final String tableName;

    @Override
    public Optional<Contexto> consultarPorTelefone(String telefone) {
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":telefone", AttributeValue.builder().s(telefone).build());

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(tableName)
                .indexName("TelefoneIndex")
                .keyConditionExpression("telefone = :telefone")
                .expressionAttributeValues(expressionValues)
                .limit(1)
                .build();

        QueryResponse response;

        try {
            response = dynamoDbClient.query(queryRequest);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_CONTEXTO_PELO_TELEFONE_E_ATIVO, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_CONTEXTO_PELO_TELEFONE_E_ATIVO, ex.getCause());
        }

        if (response.hasItems() && !response.items().isEmpty()) {
            Map<String, AttributeValue> item = response.items().get(0);
            ContextoEntity contexto = converterParaContextoEntity(item);
            return Optional.of(contexto).map(ContextoMapper::paraDomain);
        }

        return Optional.empty();
    }

    @Override
    public Contexto salvar(Contexto contexto) {
        ContextoEntity contextoEntity = ContextoMapper.paraEntity(contexto);

        try {
            contextoEntity = repository.salvar(contextoEntity);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_SALVAR_CONTEXTO, ex);
            throw new DataProviderException(MENSAGEM_ERRO_SALVAR_CONTEXTO, ex.getCause());
        }

        return ContextoMapper.paraDomain(contextoEntity);
    }

    private ContextoEntity converterParaContextoEntity(Map<String, AttributeValue> item) {
        List<MensagemContexto> mensagens = Optional.ofNullable(item.get("mensagens"))
                .map(mensagemContextoListConverter::fromAttributeValue)
                .orElseGet(Collections::emptyList);

        StatusContexto status = Optional.ofNullable(item.get("status"))
                .map(AttributeValue::s)
                .filter(s -> !s.isBlank())
                .map(StatusContexto::valueOf)
                .orElse(StatusContexto.ATIVO);

        return ContextoEntity.builder()
                .id(UUID.fromString(item.get("id").s()))
                .telefone(item.get("telefone").s())
                .mensagens(mensagens)
                .status(status)
                .build();
    }
}
