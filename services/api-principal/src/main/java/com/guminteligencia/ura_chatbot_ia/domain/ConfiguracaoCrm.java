package com.guminteligencia.ura_chatbot_ia.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
@Builder
@Embeddable
@ToString
@NoArgsConstructor
public class ConfiguracaoCrm {

    private CrmType crmType;
    private Map<String, String> mapeamentoCampos;
    private String idTagInativo;
    private String idTagAtivo;
    private String idEtapaInativos;
    private String idEtapaAtivos;


    private String acessToken;
    private String crmUrl;

    public void setAcessToken(String acessToken) {
        if(acessToken != null)
            this.acessToken = acessToken;
    }
}
