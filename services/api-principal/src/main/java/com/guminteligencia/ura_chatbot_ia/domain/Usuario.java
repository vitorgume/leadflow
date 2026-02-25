package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.*;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
public class Usuario {
    private UUID id;
    private String nome;
    private String telefone;
    private String senha;
    private String email;
    private String telefoneConectado;
    private Map<String, Object> atributosQualificacao;
    private ConfiguracaoCrm configuracaoCrm;
    private String mensagemDirecionamentoVendedor;
    private String mensagemRecontatoG1;
    private String whatsappToken;
    private String whatsappIdInstance;
    private String agenteApiKey;

    public void setWhatsappToken(String whatsappToken) {
        if(whatsappToken != null)
            this.whatsappToken = whatsappToken;
    }

    public void setWhatsappIdInstance(String whatsappIdInstance) {
        if(whatsappIdInstance != null)
            this.whatsappIdInstance = whatsappIdInstance;
    }

    public void setAgenteApiKey(String agenteApiKey) {
        if(agenteApiKey != null)
            this.agenteApiKey = agenteApiKey;
    }

    public void setDados(Usuario novosDados) {
        this.nome = novosDados.getNome();
        this.telefone = novosDados.getTelefone();
        this.senha = novosDados.getSenha();
        this.email = novosDados.getEmail();
        this.telefoneConectado = novosDados.getTelefoneConectado();
        this.atributosQualificacao = novosDados.getAtributosQualificacao();
        this.configuracaoCrm = novosDados.getConfiguracaoCrm();
        this.mensagemDirecionamentoVendedor = novosDados.getMensagemDirecionamentoVendedor();
        this.mensagemRecontatoG1 = novosDados.getMensagemRecontatoG1();
        this.whatsappToken = novosDados.getWhatsappToken();
        this.whatsappIdInstance = novosDados.getWhatsappIdInstance();
        this.agenteApiKey = novosDados.getAgenteApiKey();
    }
}
