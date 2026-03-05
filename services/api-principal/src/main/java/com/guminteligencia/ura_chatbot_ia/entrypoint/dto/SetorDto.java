package com.guminteligencia.ura_chatbot_ia.entrypoint.dto;

import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.outrosSetores.Membro;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class SetorDto {
    private UUID id;
    private String nome;
    private String descricao;
    private List<MembroDto> membros;
    private LocalDateTime dataCriacao;
    private UsuarioDto usuario;
}
