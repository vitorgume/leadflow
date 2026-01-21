package com.guminteligencia.ura_chatbot_ia.entrypoint.dto.vendedor;

import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Condicao;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ConfiguracaoEscolhaVendedorDto {
    private UUID id;
    private Usuario usuario;
    private Vendedor vendedor;
    List<Condicao> condicoes;
}
