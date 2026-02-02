package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ObjetoRelatorioDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ObjetoRelatorioEntity;

import java.util.List;

public class RelatorioMapper {
    public static List<ObjetoRelatorioDto> paraDto(List<ObjetoRelatorioEntity> entity) {
        return entity.stream().map(relatorio ->
                ObjetoRelatorioDto.builder()
                        .nome(relatorio.getNome())
                        .telefone(relatorio.getTelefone())
                        .atributos_qualificacao(relatorio.getAtributos_qualificacao())
                        .nome_vendedor(relatorio.getNome_vendedor())
                        .data_criacao(relatorio.getData_criacao())
                        .build()
        ).toList();
    }
}
