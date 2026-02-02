package com.guminteligencia.ura_chatbot_ia.entrypoint.dto.vendedor;

import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.UsuarioDto;
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
    private UsuarioDto usuario;
    private List<VendedorDto> vendedores;
    List<CondicaoDto> condicoes;
    private Integer prioridade;
}
