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
@NoArgsConstructor
public class ConfiguracaoEscolhaVendedor {
    private UUID id;
    private Usuario usuario;
    private List<Vendedor> vendedores;
    List<Condicao> condicoes;
    private Integer prioridade;

    public void setDados(ConfiguracaoEscolhaVendedor configuracaoEscolhaVendedor) {
        this.vendedores = configuracaoEscolhaVendedor.getVendedores();
        this.condicoes = configuracaoEscolhaVendedor.getCondicoes();
        this.prioridade = configuracaoEscolhaVendedor.getPrioridade();
    }
}
