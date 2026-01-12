package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity;

import com.guminteligencia.ura_chatbot_ia.domain.CrmType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor // Adicionei o NoArgsConstructor (JPA exige)
@Getter
@Setter
@Builder
@Embeddable
public class ConfiguracaoCrmEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "crm_type")
    private CrmType crmType;

    @Column(name = "mapeamento_campos")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> mapeamentoCampos;

    @Column(name = "id_tag_inativo")
    private String idTagInativo;

    @Column(name = "id_tag_ativo")
    private String idTagAtivo;

    @Column(name = "id_etapa_inativos")
    private String idEtapaInativos;

    @Column(name = "id_etapa_ativos")
    private String idEtapaAtivos;

    @Column(name = "acess_token", columnDefinition = "TEXT")
    private String acessToken;
}
