package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import com.guminteligencia.ura_chatbot_ia.domain.MensagemContexto;
import com.guminteligencia.ura_chatbot_ia.domain.StatusContexto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ContextoEntityLeadflow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ContextoMapperTest {

    private Contexto contextoDomain;
    private ContextoEntityLeadflow contextoEntityLeadflow;

    @BeforeEach
    void setUp() {
        List<MensagemContexto> mensagensDomain = List.of(
                MensagemContexto.builder().mensagem("Mensagem 1").imagemUrl("img1").audioUrl("aud1").build(),
                MensagemContexto.builder().mensagem("Mensagem 2").imagemUrl("img2").audioUrl("aud2").build()
        );
        List<MensagemContexto> mensagensEntity = List.of(
                MensagemContexto.builder().mensagem("Mensagem 1").imagemUrl("img1").audioUrl("aud1").build(),
                MensagemContexto.builder().mensagem("Mensagem 2").imagemUrl("img2").audioUrl("aud2").build(),
                MensagemContexto.builder().mensagem("Mensagem 3").imagemUrl("img3").audioUrl("aud3").build()
        );

        contextoDomain = Contexto.builder()
                .id(UUID.randomUUID())
                .telefone("000000000000")
                .mensagens(mensagensDomain)
                .status(StatusContexto.ATIVO)
                .mensagemFila(Message.builder().build())
                .build();

        contextoEntityLeadflow = ContextoEntityLeadflow.builder()
                .id(UUID.randomUUID())
                .telefone("000000000001")
                .mensagens(mensagensEntity)
                .status(StatusContexto.OBSOLETO)
                .build();
    }

    @Test
    void deveTrasnformarParaDomain() {
        Contexto contextoTeste = ContextoMapper.paraDomain(contextoEntityLeadflow);

        Assertions.assertEquals(contextoTeste.getId(), contextoEntityLeadflow.getId());
        Assertions.assertEquals(contextoTeste.getTelefone(), contextoEntityLeadflow.getTelefone());
        Assertions.assertEquals(contextoTeste.getMensagens(), contextoEntityLeadflow.getMensagens());
        Assertions.assertEquals(contextoTeste.getStatus(), contextoEntityLeadflow.getStatus());
        Assertions.assertNull(contextoTeste.getMensagemFila());
    }

    @Test
    void deveTransformarParaEntity() {
        ContextoEntityLeadflow contextoTeste = ContextoMapper.paraEntity(contextoDomain);

        Assertions.assertEquals(contextoTeste.getId(), contextoDomain.getId());
        Assertions.assertEquals(contextoTeste.getTelefone(), contextoDomain.getTelefone());
        Assertions.assertEquals(contextoTeste.getMensagens(), contextoDomain.getMensagens());
        Assertions.assertEquals(contextoTeste.getStatus(), contextoDomain.getStatus());
    }

    @Test
    void paraDomainDeMessage_deveMapearCamposEManterMensagemFila() throws Exception {
        UUID expectedId = UUID.randomUUID();
        String expectedTel = "+5511999999999";
        List<MensagemContexto> expectedMsgs = List.of(
                MensagemContexto.builder().mensagem("oi").imagemUrl("img1").audioUrl(null).build(),
                MensagemContexto.builder().mensagem("tchau").imagemUrl(null).audioUrl("aud2").build()
        );
        StatusContexto expectedStatus = StatusContexto.OBSOLETO;

        String json = String.format(
                """
                {
                  "id":       "%s",
                  "telefone": "%s",
                  "mensagens": %s,
                  "status":   "%s"
                }
                """,
                expectedId,
                expectedTel,
                new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(expectedMsgs),
                expectedStatus.name()
        );

        Message message = Message.builder()
                .messageId(UUID.randomUUID().toString())
                .receiptHandle("rh-" + UUID.randomUUID())
                .body(json)
                .build();

        Contexto ctx = ContextoMapper.paraDomainDeMessage(message);

        assertAll("Contexto",
                () -> assertEquals(expectedId,   ctx.getId(),            "id deve vir do JSON"),
                () -> assertEquals(expectedTel,  ctx.getTelefone(),      "telefone deve vir do JSON"),
                () -> assertEquals(expectedMsgs, ctx.getMensagens(),     "mensagens deve vir do JSON"),
                () -> assertEquals(expectedStatus, ctx.getStatus(),      "status deve vir do JSON"),
                () -> assertSame(message,        ctx.getMensagemFila(),  "deve guardar a mesma instância de Message")
        );
    }

    @Test
    void paraDomainDeMessage_jsonInvalido_deveLancarRuntimeException() {
        Message bad = Message.builder()
                .messageId(UUID.randomUUID().toString())
                .receiptHandle("rh-"+UUID.randomUUID())
                .body("isso não é JSON")
                .build();

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                ContextoMapper.paraDomainDeMessage(bad)
        );
        assertTrue(ex.getMessage().contains("Erro ao converter mensagem da fila para Contexto"));
        assertNotNull(ex.getCause());
    }
}
