package com.guminteligencia.ura_chatbot_ia.domain.vendedor;

import lombok.*;

import java.util.UUID;


@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
public class Condicao {
    private UUID id;
    private String campo;
    private OperadorLogico operadorLogico;
    private String valor;
    private ConectorLogico conectorLogico;

    public void setDados(Condicao condicao) {
        this.campo = condicao.getCampo();
        this.operadorLogico = condicao.getOperadorLogico();
        this.valor = condicao.getValor();
        this.conectorLogico = condicao.getConectorLogico();
    }
}
