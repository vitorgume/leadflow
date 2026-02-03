package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ObjetoRelatorioDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.objetoRelatorio.RelatorioProjection;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class RelatorioMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Recebe a lista da Interface RelatorioProjection
    public static List<ObjetoRelatorioDto> paraDto(List<RelatorioProjection> projections) {
        return projections.stream().map(proj ->
                ObjetoRelatorioDto.builder()
                        .nome(proj.getNome())
                        .telefone(proj.getTelefone())
                        .atributos_qualificacao(parseJsonToMap(proj.getAtributosQualificacao()))
                        .nome_vendedor(proj.getNomeVendedor())
                        .data_criacao(proj.getDataCriacao())
                        .build()
        ).toList();
    }

    // ... método parseJsonToMap continua igual

    private static Map<String, String> parseJsonToMap(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("Erro ao converter a string JSON para Map no RelatorioMapper. JSON: {}", json, e);
            return Collections.emptyMap();
        }
    }
}