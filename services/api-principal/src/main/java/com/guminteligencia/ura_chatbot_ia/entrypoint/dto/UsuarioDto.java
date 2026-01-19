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
    private String mensagemDirecionamentoVendedor;
    private String mensagemRecontatoG1;
}
