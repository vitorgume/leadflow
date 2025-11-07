package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Cliente {
    private UUID id;
    private String nome;
    private String telefone;
    private String cpf;
    private Boolean consentimentoAtendimnento;
    private TipoConsulta tipoConsulta;
    private String dorDesejoPaciente;
    private String linkMidia;
    private PreferenciaHorario preferenciaHorario;
    private boolean inativo;

    public void setDados(Cliente cliente) {
        this.nome = cliente.getNome();
        this.cpf = cliente.getCpf();
        this.consentimentoAtendimnento = cliente.getConsentimentoAtendimnento();
        this.tipoConsulta = cliente.getTipoConsulta();
        this.dorDesejoPaciente = cliente.getDorDesejoPaciente();
        this.linkMidia = cliente.getLinkMidia();
        this.preferenciaHorario = cliente.getPreferenciaHorario();
    }
}
