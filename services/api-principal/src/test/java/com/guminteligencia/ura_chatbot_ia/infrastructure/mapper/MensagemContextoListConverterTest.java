package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.MensagemContexto;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MensagemContextoListConverterTest {

    private final MensagemContextoListConverter converter = new MensagemContextoListConverter();

    @Test
    void deveTransformarListaParaAttributeValue() {
        List<MensagemContexto> mensagens = List.of(
                MensagemContexto.builder().mensagem("Oi").imagemUrl("img").audioUrl("aud").build()
        );

        AttributeValue attributeValue = converter.transformFrom(mensagens);

        // round-trip serialization
        AttributeValue expected = AttributeValue.builder().s(attributeValue.s()).build();
        assertEquals(expected, attributeValue);

        List<MensagemContexto> resultado = converter.transformTo(attributeValue);
        assertEquals(mensagens, resultado);
    }

    @Test
    void deveRetornarNullAttributeQuandoListaVaziaOuNula() {
        AttributeValue vazio = converter.transformFrom(Collections.emptyList());
        AttributeValue nulo = converter.transformFrom(null);

        assertEquals(Boolean.TRUE, vazio.nul());
        assertEquals(Boolean.TRUE, nulo.nul());
    }

    @Test
    void deveRetornarListaVaziaQuandoAttributeNuloOuNullFlag() {
        assertEquals(Collections.emptyList(), converter.transformTo(null));
        assertEquals(Collections.emptyList(), converter.transformTo(AttributeValue.builder().nul(true).build()));
    }

    @Test
    void deveLancarExcecaoQuandoJsonInvalido() {
        AttributeValue atributoInvalido = AttributeValue.builder().s("nao e json").build();
        assertThrows(RuntimeException.class, () -> converter.transformTo(atributoInvalido));
    }
}
