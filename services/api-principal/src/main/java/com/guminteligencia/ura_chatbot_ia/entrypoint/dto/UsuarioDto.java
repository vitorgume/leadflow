package com.guminteligencia.ura_chatbot_ia.entrypoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class UsuarioDto {
    private UUID id;
    private String nome;
    private String telefone;
    private String senha;
    private String email;

    @JsonProperty("telefone_conectado")
    private String telefoneConectado;

    @JsonProperty("atributos_qualificacao")
    private Map<String, Object> atributosQualificacao;

    @JsonProperty("configuracao_crm")
    private ConfiguracaoCrmDto configuracaoCrm;

    @JsonProperty("mensagem_direcionamento_vendedor")
    private String mensagemDirecionamentoVendedor;

    @JsonProperty("mensagem_recontato_g1")
    private String mensagemRecontatoG1;

    @JsonProperty("whatsapp_token")
    private String whatsappToken;

    @JsonProperty("whatsapp_id_instance")
    private String whatsappIdInstance;

    @JsonProperty("agente_api_key")
    private String agenteApiKey;
}
