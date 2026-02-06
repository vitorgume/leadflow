package com.gumeinteligencia.api_intermidiaria.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ConversaAgente {
    private UUID id;
    private Cliente cliente;
    private LocalDateTime dataCriacao;
    private Boolean finalizada;
    private LocalDateTime dataUltimaMensagem;
    private Boolean recontato;
}
