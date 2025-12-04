package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.*;
import software.amazon.awssdk.services.sqs.model.Message;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AvisoContexto {
    private UUID id;
    private LocalDateTime dataCriacao;
    private UUID idContexto;
    private Message mensagemFila;
}
