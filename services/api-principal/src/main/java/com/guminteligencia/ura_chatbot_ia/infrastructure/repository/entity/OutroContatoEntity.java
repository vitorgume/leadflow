package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity;

import com.guminteligencia.ura_chatbot_ia.domain.TipoContato;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

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
    private UUID idUsuario;

    @DynamoDbPartitionKey
    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "TelefoneIndex")
    @DynamoDbSecondarySortKey(indexNames = {"UsuarioTelefoneIndex"})
    public String getTelefone() {
        return telefone;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoContato getTipoContato() {
        return tipoContato;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"UsuarioTelefoneIndex"})
    public UUID getIdUsuario() {
        return idUsuario;
    }

}
