package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.AvisoContexto;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.model.Message;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AvisoContextoMapperTest {

    @Test
    void deveConverterMensagemComCampoMessageAninhado() {
        UUID id = UUID.randomUUID();
        UUID idContexto = UUID.randomUUID();
        LocalDateTime dataCriacao = LocalDateTime.of(2025, 1, 1, 12, 0);

        String payload = String.format(
                "{\"Message\":\"{\\\"id\\\":\\\"%s\\\",\\\"dataCriacao\\\":\\\"%s\\\",\\\"idContexto\\\":\\\"%s\\\"}\"}",
                id, dataCriacao, idContexto
        );
        Message message = Message.builder().body(payload).build();

        AvisoContexto aviso = AvisoContextoMapper.paraDomainDeMessage(message);

        assertEquals(id, aviso.getId());
        assertEquals(dataCriacao, aviso.getDataCriacao());
        assertEquals(idContexto, aviso.getIdContexto());
        assertEquals(message, aviso.getMensagemFila());
    }

    @Test
    void deveConverterMensagemSemCampoMessage() {
        UUID id = UUID.randomUUID();
        UUID idContexto = UUID.randomUUID();
        LocalDateTime dataCriacao = LocalDateTime.of(2025, 2, 2, 10, 30);

        String payload = String.format(
                "{\"id\":\"%s\",\"dataCriacao\":\"%s\",\"idContexto\":\"%s\"}",
                id, dataCriacao, idContexto
        );
        Message message = Message.builder().body(payload).build();

        AvisoContexto aviso = AvisoContextoMapper.paraDomainDeMessage(message);

        assertEquals(id, aviso.getId());
        assertEquals(dataCriacao, aviso.getDataCriacao());
        assertEquals(idContexto, aviso.getIdContexto());
        assertEquals(message, aviso.getMensagemFila());
    }

    @Test
    void deveLancarExcecaoParaJsonInvalido() {
        Message message = Message.builder().body("{invalid-json").build();
        assertThrows(RuntimeException.class, () -> AvisoContextoMapper.paraDomainDeMessage(message));
    }
}
