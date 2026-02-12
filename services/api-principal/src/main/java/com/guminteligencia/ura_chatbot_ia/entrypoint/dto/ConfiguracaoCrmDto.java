package com.guminteligencia.ura_chatbot_ia.entrypoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ConfiguracaoCrmDto {

    @JsonProperty("crm_type")
    private CrmType crmType;

    @JsonProperty("mapeamento_campos")
    private Map<String, String> mapeamentoCampos;

    @JsonProperty("id_tag_inativo")
    private String idTagInativo;

    @JsonProperty("id_tag_ativo")
    private String idTagAtivo;

    @JsonProperty("id_etapa_inativos")
    private String idEtapaInativos;

    @JsonProperty("id_etapa_ativos")
    private String idEtapaAtivos;

    @JsonProperty("acess_token")
    private String acessToken;

    @JsonProperty("crm_url")
    private String crmUrl;
}
