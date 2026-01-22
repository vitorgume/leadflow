package com.guminteligencia.ura_chatbot_ia.domain.vendedor;

import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ConfiguracaoEscolhaVendedor {
    private UUID id;
    private Usuario usuario;
    private List<Vendedor> vendedores;
    List<Condicao> condicoes;

    public void setDados(ConfiguracaoEscolhaVendedor configuracaoEscolhaVendedor) {
        this.usuario = configuracaoEscolhaVendedor.getUsuario();
        this.vendedores = configuracaoEscolhaVendedor.getVendedores();
        this.condicoes = configuracaoEscolhaVendedor.getCondicoes();
    }
}
