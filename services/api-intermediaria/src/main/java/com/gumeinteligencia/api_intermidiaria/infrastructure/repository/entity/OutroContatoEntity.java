package com.gumeinteligencia.api_intermidiaria.infrastructure.repository.entity;

import com.gumeinteligencia.api_intermidiaria.domain.outroContato.TipoContato;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.util.UUID;

@DynamoDbBean
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class OutroContatoEntity {

    private UUID id;
    private String nome;
    private String telefone;
    private String descricao;
    private TipoContato tipoContato;

    @DynamoDbPartitionKey
    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
    @DynamoDbSecondaryPartitionKey(indexNames = "TelefoneIndex")
    public String getTelefone() {
        return telefone;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoContato getTipoContato() {
        return tipoContato;
    }
}
