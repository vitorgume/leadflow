package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.*;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
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
    private String mensagemEncaminhamento;
    private String whatsappToken;
    private String whatsappIdInstance;
    private String agenteApiKey;

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", telefone='" + telefone + '\'' +
                ", telefoneConectado='" + telefoneConectado + '\'' +
                ", atributosQualificacao=" + atributosQualificacao +
                ", configuracaoCrm=" + configuracaoCrm +
                ", whatsappToken='" + whatsappToken + '\'' +
                ", whatsappIdInstance='" + whatsappIdInstance + '\'' +
                ", agenteApiKey='" + agenteApiKey + '\'' +
                '}';
    }
}
