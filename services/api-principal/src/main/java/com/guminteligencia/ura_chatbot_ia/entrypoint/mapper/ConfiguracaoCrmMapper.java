package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ConfiguracaoCrmDto;

public class ConfiguracaoCrmMapper {

    public static ConfiguracaoCrm paraDomain(ConfiguracaoCrmDto dto) {
        if(dto == null) {
            return null;
        } else {
            return ConfiguracaoCrm.builder()
                    .crmType(dto.getCrmType())
                    .mapeamentoCampos(dto.getMapeamentoCampos())
                    .idTagInativo(dto.getIdTagInativo())
                    .idTagAtivo(dto.getIdTagAtivo())
                    .idEtapaInativos(dto.getIdEtapaInativos())
                    .idEtapaAtivos(dto.getIdEtapaAtivos())
                    .acessToken(dto.getAcessToken())
                    .crmUrl(dto.getCrmUrl())
                    .build();
        }
    }

    public static ConfiguracaoCrmDto paraDto(ConfiguracaoCrm domain) {
        if(domain == null) {
            return null;
        } else {
            return ConfiguracaoCrmDto.builder()
                    .crmType(domain.getCrmType())
                    .mapeamentoCampos(domain.getMapeamentoCampos())
                    .idTagInativo(domain.getIdTagInativo())
                    .idTagAtivo(domain.getIdTagAtivo())
                    .idEtapaInativos(domain.getIdEtapaInativos())
                    .idEtapaAtivos(domain.getIdEtapaAtivos())
                    .crmUrl(domain.getCrmUrl())
                    .acessToken(domain.getAcessToken())
                    .build();
        }
    }
}
