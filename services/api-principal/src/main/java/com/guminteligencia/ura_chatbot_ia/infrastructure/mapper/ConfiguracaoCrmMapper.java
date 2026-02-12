package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.ConfiguracaoCrm;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConfiguracaoCrmEntity;

public class ConfiguracaoCrmMapper {

    public static ConfiguracaoCrm paraDomain(ConfiguracaoCrmEntity entity) {
        return ConfiguracaoCrm.builder()
                .crmType(entity.getCrmType())
                .mapeamentoCampos(entity.getMapeamentoCampos())
                .idTagInativo(entity.getIdTagInativo())
                .idTagAtivo(entity.getIdTagAtivo())
                .idEtapaInativos(entity.getIdEtapaInativos())
                .idEtapaAtivos(entity.getIdEtapaAtivos())
                .acessToken(entity.getAcessToken())
                .crmUrl(entity.getCrmUrl())
                .build();
    }

    public static ConfiguracaoCrmEntity paraEntity(ConfiguracaoCrm domain) {
        return ConfiguracaoCrmEntity.builder()
                .crmType(domain.getCrmType())
                .mapeamentoCampos(domain.getMapeamentoCampos())
                .idTagInativo(domain.getIdTagInativo())
                .idTagAtivo(domain.getIdTagAtivo())
                .idEtapaInativos(domain.getIdEtapaInativos())
                .idEtapaAtivos(domain.getIdEtapaAtivos())
                .acessToken(domain.getAcessToken())
                .crmUrl(domain.getCrmUrl())
                .build();
    }
}
